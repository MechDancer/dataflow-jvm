package org.mechdancer.dataflow.core

import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.stub
import org.mechdancer.dataflow.core.internal.view
import java.util.*

/**
 * 默认源节点（虚拟源节点）
 * 为来自外部的事件提供堆
 */
class DefaultSource<T>(owner: ITarget<T>) : ISource<T> {
	override val uuid = UUID.randomUUID()!!
	private val link = Link(this, owner, linkOptions())
	override val name = "default source of ${owner.view()}"

	override fun consume(id: Long, link: Link<T>) = core.consume(id)

	override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) = stub("这个方法没有任何作用")

	private val core = SourceCore<T>()

	fun offer(event: T) = core.offer(event) to link
}
