package org.mechdancer.dataflow.core

import org.mechdancer.dataflow.core.internal.Link
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.view
import java.util.*

/**
 * 默认源节点（虚拟源节点）
 * 为来自外部的事件提供堆
 */
class DefaultSource<T>(owner: ITarget<T>) : ISource<T> {
    override val uuid = UUID.randomUUID()!!
    override val name = "default source of ${owner.view()}"

    override fun consume(id: Long) = core.consume(id)

    private val core = SourceCore<T>(Int.MAX_VALUE)
    private val link = Link(this, owner, LinkOptions())

    fun offer(event: T) = core.offer(event) to link
}
