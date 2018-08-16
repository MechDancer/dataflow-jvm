package org.mechdancer.dataflow.core

import org.mechdancer.dataflow.core.internal.view
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.atomic.AtomicLong

/**
 * 链接信息
 * @param source 事件源
 * @param target 事件宿
 * @param options 链接选项
 */
class Link<T> internal constructor(
		val source: ISource<T>,
		val target: ITarget<T>,
		val options: LinkOptions<T>
) : Comparable<Link<*>> {
	override fun compareTo(other: Link<*>) = uuid.compareTo(other.uuid)

	//唯一标识符
	val uuid = UUID.randomUUID()!!

	//构造时加入列表
	init {
		if (this.source !is DefaultSource<T>) {
			list.add(this)
			changed.post(list.toList())
		}
	}

	//对通过链接的事件计数
	private val _eventCount = AtomicLong(0)
	val eventCount get() = _eventCount.get()

	fun offer(id: Long) = target.offer(id, this)
	fun consume(id: Long) =
			source.consume(id).apply {
				if (this.first && _eventCount.incrementAndGet() > options.eventLimit)
					dispose()
			}

	//断开链接
	fun dispose() = list.remove(this).also { if (it) changed.post(list.toList()) }

	override fun toString() = "[$uuid]: ${source.view()} -> ${target.view()}"

	companion object {
		//全局链接列表
		private val list = ConcurrentSkipListSet<Link<*>>()

		//拓扑改变事件
		val changed = broadcast<List<Link<*>>>("LinkInfo")

		fun view() = list.toList()

		//按源从列表中查找
		fun <T> find(source: ISource<T>) =
				@Suppress("UNCHECKED_CAST")
				list.filter { it.source === source }.map { it as Link<T> }
	}
}
