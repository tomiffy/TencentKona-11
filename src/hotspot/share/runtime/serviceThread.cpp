/*
 * Copyright (c) 2012, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 */

#include "precompiled.hpp"
#include "classfile/protectionDomainCache.hpp"
#include "classfile/stringTable.hpp"
#include "classfile/symbolTable.hpp"
#include "classfile/systemDictionary.hpp"
#include "runtime/interfaceSupport.inline.hpp"
#include "runtime/javaCalls.hpp"
#include "runtime/serviceThread.hpp"
#include "runtime/mutexLocker.hpp"
#include "runtime/os.hpp"
#include "prims/jvmtiImpl.hpp"
#include "prims/resolvedMethodTable.hpp"
#include "services/diagnosticArgument.hpp"
#include "services/diagnosticFramework.hpp"
#include "services/gcNotifier.hpp"
#include "services/lowMemoryDetector.hpp"

ServiceThread* ServiceThread::_instance = NULL;
JvmtiDeferredEvent* ServiceThread::_jvmti_event = NULL;
// The service thread has it's own static deferred event queue.
// Events can be posted before JVMTI vm_start, so it's too early to call JvmtiThreadState::state_for
// to add this field to the per-JavaThread event queue.  TODO: fix this sometime later
JvmtiDeferredEventQueue ServiceThread::_jvmti_service_queue;

void ServiceThread::initialize() {
  EXCEPTION_MARK;

  const char* name = "Service Thread";
  Handle string = java_lang_String::create_from_str(name, CHECK);

  // Initialize thread_oop to put it into the system threadGroup
  Handle thread_group (THREAD, Universe::system_thread_group());
  Handle thread_oop = JavaCalls::construct_new_instance(
                          SystemDictionary::Thread_klass(),
                          vmSymbols::threadgroup_string_void_signature(),
                          thread_group,
                          string,
                          CHECK);

  {
    MutexLocker mu(Threads_lock);
    ServiceThread* thread =  new ServiceThread(&service_thread_entry);

    // At this point it may be possible that no osthread was created for the
    // JavaThread due to lack of memory. We would have to throw an exception
    // in that case. However, since this must work and we do not allow
    // exceptions anyway, check and abort if this fails.
    if (thread == NULL || thread->osthread() == NULL) {
      vm_exit_during_initialization("java.lang.OutOfMemoryError",
                                    os::native_thread_creation_failed_msg());
    }

    java_lang_Thread::set_thread(thread_oop(), thread);
    java_lang_Thread::set_priority(thread_oop(), NearMaxPriority);
    java_lang_Thread::set_daemon(thread_oop());
    thread->set_threadObj(thread_oop());
    _instance = thread;

    Threads::add(thread);
    Thread::start(thread);
  }
}

void ServiceThread::service_thread_entry(JavaThread* jt, TRAPS) {
  while (true) {
    bool sensors_changed = false;
    bool has_jvmti_events = false;
    bool has_gc_notification_event = false;
    bool has_dcmd_notification_event = false;
    bool acs_notify = false;
    bool stringtable_work = false;
    bool symboltable_work = false;
    bool resolved_method_table_work = false;
    bool protection_domain_table_work = false;
    JvmtiDeferredEvent jvmti_event;
    {
      // Need state transition ThreadBlockInVM so that this thread
      // will be handled by safepoint correctly when this thread is
      // notified at a safepoint.

      // This ThreadBlockInVM object is not also considered to be
      // suspend-equivalent because ServiceThread is not visible to
      // external suspension.

      ThreadBlockInVM tbivm(jt);

      MutexLockerEx ml(Service_lock, Mutex::_no_safepoint_check_flag);
      while (!(sensors_changed = LowMemoryDetector::has_pending_requests()) &&
             !(has_jvmti_events = _jvmti_service_queue.has_events()) &&
              !(has_gc_notification_event = GCNotifier::has_event()) &&
              !(has_dcmd_notification_event = DCmdFactory::has_pending_jmx_notification()) &&
              !(stringtable_work = StringTable::has_work()) &&
              !(symboltable_work = SymbolTable::has_work()) &&
              !(resolved_method_table_work = ResolvedMethodTable::has_work()) &&
              !(protection_domain_table_work = SystemDictionary::pd_cache_table()->has_work())) {
        // wait until one of the sensors has pending requests, or there is a
        // pending JVMTI event or JMX GC notification to post
        Service_lock->wait(Mutex::_no_safepoint_check_flag);
      }

      if (has_jvmti_events) {
        // Get the event under the Service_lock
        jvmti_event = _jvmti_service_queue.dequeue();
        _jvmti_event = &jvmti_event;
      }
    }

    if (stringtable_work) {
      StringTable::do_concurrent_work(jt);
    }

    if (symboltable_work) {
      SymbolTable::do_concurrent_work(jt);
    }

    if (has_jvmti_events) {
      _jvmti_event->post();
      _jvmti_event = NULL;  // reset
    }

    if (sensors_changed) {
      LowMemoryDetector::process_sensor_changes(jt);
    }

    if(has_gc_notification_event) {
      GCNotifier::sendNotification(CHECK);
    }

    if(has_dcmd_notification_event) {
      DCmdFactory::send_notification(CHECK);
    }

    if (resolved_method_table_work) {
      ResolvedMethodTable::unlink();
    }

    if (protection_domain_table_work) {
      SystemDictionary::pd_cache_table()->unlink();
    }
  }
}

void ServiceThread::enqueue_deferred_event(JvmtiDeferredEvent* event) {
  MutexLockerEx ml(Service_lock, Mutex::_no_safepoint_check_flag);
  // If you enqueue events before the service thread runs, gc and the sweeper
  // cannot keep the nmethod alive.  This could be restricted to compiled method
  // load and unload events, if we wanted to be picky.
  assert(_instance != NULL, "cannot enqueue events before the service thread runs");
  _jvmti_service_queue.enqueue(*event);
  Service_lock->notify_all();
 }

void ServiceThread::oops_do(OopClosure* f, CodeBlobClosure* cf) {
  JavaThread::oops_do(f, cf);
  // The ServiceThread "owns" the JVMTI Deferred events, scan them here
  // to keep them alive until they are processed.
  if (cf != NULL) {
    if (_jvmti_event != NULL) {
      _jvmti_event->oops_do(f, cf);
    }
    // Requires a lock, because threads can be adding to this queue.
    MutexLockerEx ml(Service_lock, Mutex::_no_safepoint_check_flag);
    _jvmti_service_queue.oops_do(f, cf);
  }
}

void ServiceThread::nmethods_do(CodeBlobClosure* cf) {
  JavaThread::nmethods_do(cf);
  if (cf != NULL) {
    if (_jvmti_event != NULL) {
      _jvmti_event->nmethods_do(cf);
    }
    // Requires a lock, because threads can be adding to this queue.
    MutexLockerEx ml(Service_lock, Mutex::_no_safepoint_check_flag);
    _jvmti_service_queue.nmethods_do(cf);
  }
}
