package site.syzk.dataflow.core

import site.syzk.dataflow.core.internal.SourceCore

/**
 * 默认源节点（虚拟源节点）
 * 为来自外部的事件提供堆
 */
class DefaultSource<T>(owner: ITarget<T>) : ISource<T> {
    private val link = Link(this, owner, linkOptions())

    override fun consume(id: Long, link: Link<T>) = core.consume(id)

    override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) = throw Exception("这个方法没有任何作用")
    override fun unlink(target: ITarget<T>) = throw Exception("这个方法没有任何作用")

    private val core = SourceCore<T>()
    fun offer(event: T) = core.offer(event) to link
}
