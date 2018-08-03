package site.syzk.dataflow.core

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(CLASS, FUNCTION)
@Retention(SOURCE)
annotation class ThreadSafety(val value: Boolean)
