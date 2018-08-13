package org.mechdancer.dataflow.core

import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.stub

/**
 * 默认源节点（虚拟源节点）
 * 为来自外部的事件提供堆
 */
class DefaultSource<T>(owner: org.mechdancer.dataflow.core.ITarget<T>) : org.mechdancer.dataflow.core.ISource<T> {
	private val link = org.mechdancer.dataflow.core.Link(this, owner, org.mechdancer.dataflow.core.linkOptions())
	override val name = "default source of ${owner.name}"

	override fun consume(id: Long, link: org.mechdancer.dataflow.core.Link<T>) = core.consume(id)

	override fun linkTo(target: org.mechdancer.dataflow.core.ITarget<T>, options: org.mechdancer.dataflow.core.LinkOptions<T>) = stub("这个方法没有任何作用")
	override fun unlink(link: org.mechdancer.dataflow.core.Link<T>) = stub("这个方法没有任何作用")

	private val core = SourceCore<T>()

	fun offer(event: T) = core.offer(event) to link
}
