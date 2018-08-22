package org.mechdancer.dataflow.core

data class LinkInfo<T>(
    val source: ISource<T>,
    val target: ITarget<T>,
    val options: LinkOptions<T> = LinkOptions()
) {
    internal infix fun buildIn(subNet: String) =
        Link(source, target, options, subNet)
}
