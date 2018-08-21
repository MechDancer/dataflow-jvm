package org.mechdancer.dataflow.core

data class LinkOptions<T>
internal constructor(
        val predicate: (T) -> Boolean = { true },
        val eventLimit: Int = Int.MAX_VALUE
)
