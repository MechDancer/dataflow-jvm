package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.ILink.Companion.changed
import org.mechdancer.dataflow.core.ILink.Companion.list
import java.util.concurrent.atomic.AtomicInteger

/**
 * 链接信息
 * @param source 事件源
 * @param target 事件宿
 * @param options 链接选项
 */
internal class Link<T>(
	override val source: ISource<T>,
	override val target: ITarget<T>,
	override val options: LinkOptions<T>,
	private val holder: LinkManager<T>
) : ILink<T> {
	override val uuid = randomUUID()

	//构造时加入列表
	init {
		ILink.list.add(this)
		changed.post(list.toList())
	}

	private val _count = AtomicInteger(0)
	override val count get() = _count.get()
	override val rest get() = options.eventLimit - _count.get()

	override suspend infix fun offer(id: Long) = target.offer(id, this)
	override infix fun consume(id: Long) =
		source.consume(id).apply {
			if (this.hasValue && _count.incrementAndGet() > options.eventLimit)
				dispose()
		}

	override fun dispose() {
		holder.remove(this)
		list.remove(this).also { if (it) changed.post(list.toList()) }
	}

	override fun toString() = "[$uuid]: ${source.view()} -> ${target.view()}"
}
