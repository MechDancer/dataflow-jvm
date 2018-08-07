package site.syzk.dataflow.annotations

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

@Target(CLASS)
@Retention(SOURCE)
annotation class ThreadSafe(val safety: Boolean)
