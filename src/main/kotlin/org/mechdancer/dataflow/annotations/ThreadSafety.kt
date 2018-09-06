package org.mechdancer.dataflow.annotations

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * 线程安全性说明
 */
@Target(CLASS)
@Retention(SOURCE)
annotation class ThreadSafety
