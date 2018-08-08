package site.syzk.dataflow.core.internal

import site.syzk.dataflow.annotations.ThreadSafe
import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.Link
import site.syzk.dataflow.core.LinkOptions

@ThreadSafe
internal class LinkManager<T>(private val owner: ISource<T>) {
    private val _links = mutableListOf<Link<T>>()
    val links get() = _links.toList()

    fun build(target: ITarget<T>, options: LinkOptions<T>): Link<T> {
        val link = Link(owner, target, options)
        synchronized(_links) { _links.add(link) }
        return link
    }

    fun cancel(link: Link<T>) {
        synchronized(_links) { _links.remove(link) }
    }
}
