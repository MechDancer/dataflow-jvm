package org.mechdancer.dataflow.core

data class Message<T>(
    val hasValue: Boolean,
    private val content: T?
) {
    val value @Suppress("UNCHECKED_CAST") get() = content as T
}
