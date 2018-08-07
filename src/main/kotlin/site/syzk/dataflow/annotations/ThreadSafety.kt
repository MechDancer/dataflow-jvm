package site.syzk.dataflow.annotations

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * 指示线程安全性
 */
@Target(CLASS)
@Retention(SOURCE)
annotation class ThreadSafety(val safety: Boolean)
