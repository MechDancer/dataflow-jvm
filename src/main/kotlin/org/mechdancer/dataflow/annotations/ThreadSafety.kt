package org.mechdancer.dataflow.annotations

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Thread safety instruction
 *
 * Class annotated this annotation implies that this class is thread-safe.
 */
@Target(CLASS)
@Retention(SOURCE)
annotation class ThreadSafety
