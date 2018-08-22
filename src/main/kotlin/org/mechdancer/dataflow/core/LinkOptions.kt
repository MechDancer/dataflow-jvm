package org.mechdancer.dataflow.core

data class LinkOptions<T>(
        val predicate: (T) -> Boolean = { true },
        val eventLimit: Int = Int.MAX_VALUE,
        val subNet: String = ""
)
