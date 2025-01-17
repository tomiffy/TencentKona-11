/*
 * Copyright (c) 2005, 2018, Oracle and/or its affiliates. All rights reserved.
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

#ifndef OS_LINUX_VM_GLOBALS_LINUX_HPP
#define OS_LINUX_VM_GLOBALS_LINUX_HPP

//
// Defines Linux specific flags. They are not available on other platforms.
//
#define RUNTIME_OS_FLAGS(develop, \
                         develop_pd, \
                         product, \
                         product_pd, \
                         diagnostic, \
                         diagnostic_pd, \
                         notproduct, \
                         range, \
                         constraint, \
                         writeable) \
                                                                        \
  product(bool, UseOprofile, false,                                     \
        "enable support for Oprofile profiler")                         \
                                                                        \
  /*  NB: The default value of UseLinuxPosixThreadCPUClocks may be   */ \
  /* overridden in Arguments::parse_each_vm_init_arg.                */ \
  product(bool, UseLinuxPosixThreadCPUClocks, true,                     \
          "enable fast Linux Posix clocks where available")             \
                                                                        \
  product(bool, UseHugeTLBFS, false,                                    \
          "Use MAP_HUGETLB for large pages")                            \
                                                                        \
  product(bool, UseTransparentHugePages, false,                         \
          "Use MADV_HUGEPAGE for large pages")                          \
                                                                        \
  product(bool, LoadExecStackDllInVMThread, true,                       \
          "Load DLLs with executable-stack attribute in the VM Thread") \
                                                                        \
  product(bool, UseSHM, false,                                          \
          "Use SYSV shared memory for large pages")                     \
                                                                        \
  product(bool, UseContainerSupport, true,                              \
          "Enable detection and runtime container configuration support") \
                                                                        \
  product(bool, IgnoreNoShareValue, false,                              \
          "Ignore 1024 as no share")                                    \
                                                                        \
  product(uintx, CPUShareScaleFactor, 1,                                \
          "Scale cpu count when share is used")                         \
                                                                        \
  product(uintx, CPUShareScaleLimit, 0,                                 \
          "Scale cpu count limit, max is active cpu count")             \
  product(bool, PreferContainerQuotaForCPUCount, true,                  \
          "Calculate the container CPU availability based on the value" \
          " of quotas (if set), when true. Otherwise, use the CPU"    \
          " shares value, provided it is less than quota.")             \
                                                                        \
  diagnostic(bool, DumpPrivateMappingsInCore, true,                     \
          "If true, sets bit 2 of /proc/PID/coredump_filter, thus "     \
          "resulting in file-backed private mappings of the process to "\
          "be dumped into the corefile.")                               \
                                                                        \
  diagnostic(bool, DumpSharedMappingsInCore, true,                      \
          "If true, sets bit 3 of /proc/PID/coredump_filter, thus "     \
          "resulting in file-backed shared mappings of the process to " \
          "be dumped into the corefile.")                               \
                                                                        \
  diagnostic(bool, UseCpuAllocPath, false,                              \
             "Use CPU_ALLOC code path in os::active_processor_count ")

//
// Defines Linux-specific default values. The flags are available on all
// platforms, but they may have different default values on other platforms.
//
define_pd_global(bool, UseLargePages, false);
define_pd_global(bool, UseLargePagesIndividualAllocation, false);
define_pd_global(bool, UseOSErrorReporting, false);
define_pd_global(bool, UseThreadPriorities, true) ;

#endif // OS_LINUX_VM_GLOBALS_LINUX_HPP
