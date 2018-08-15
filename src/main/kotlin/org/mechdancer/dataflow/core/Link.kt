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
	val uuid = UUID.randomUUID()

	//构造时加入列表
	init {
		list.add(this)
	}

	//对通过链接的事件计数
	private val _eventCount = AtomicLong(0)
	val eventCount get() = _eventCount.get()

	//记录一个事件通过了节点
	//若达到上限则断开链接
	fun record() {
		if (_eventCount.incrementAndGet() > options.eventLimit)
			dispose()
	}

	//断开链接
	fun dispose() = list.remove(this)

	override fun toString() = "$[$uuid]: ${source.view()} -> ${target.view()}"

	companion object {
		//全局链接列表
		private val list = ConcurrentSkipListSet<Link<*>>()

		fun view() = list.toList()
	}
}
