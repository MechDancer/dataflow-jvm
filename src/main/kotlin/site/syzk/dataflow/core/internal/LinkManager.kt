package site.syzk.dataflow.core.internal

import site.syzk.dataflow.annotations.ThreadSafe
import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.Link

@ThreadSafe
internal class LinkManager<T>(private val owner: ISource<T>) {
    private val _links = mutableListOf<Link<T>>()

    /**
     * 宿节点列表
     */
    val targets get() = _links.map { it.target }

    /**
     * 添加链接
     */
    fun linkTo(target: ITarget<T>): Link<T> {
        val link = Link(owner, target)
        synchronized(_links) { _links.add(link) }
        return link
    }

    /**
     * 取消链接
     */
    fun unlink(target: ITarget<T>) {
        synchronized(_links) { _links.removeAll { it.target === target } }
    }
}
