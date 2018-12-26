package org.mechdancer.dataflow.core

/** 链接可选项 */
data class LinkOptions<T>(
        val predicate: (T) -> Boolean = { true },
        val eventLimit: Int = Int.MAX_VALUE
)
