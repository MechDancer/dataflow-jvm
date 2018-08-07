package site.syzk.dataflow.core

import site.syzk.dataflow.core.internal.SourceCore

/**
 * 默认源节点（虚拟源节点）
 * 为来自外部的事件提供堆
 */
class DefaultSource<T> : ISource<T> {
    private val core = SourceCore<T>()

    fun offer(event: T) = core.offer(event)
    override fun consume(id: Long) = core.consume(id)

    override fun linkTo(target: ITarget<T>) = throw Exception("这个方法没有任何用处")
    override fun unlink(target: ITarget<T>) = throw Exception("这个方法没有任何用处")
}
