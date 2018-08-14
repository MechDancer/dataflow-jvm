package org.mechdancer.dataflow.core

import org.mechdancer.dataflow.core.internal.view
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.atomic.AtomicLong

/**
 * 链接信息
 * @param source 事件源
 * @param target 事件宿
 * @param options 链接选项
 */
class Link<T>(
		val source: ISource<T>,
		val target: ITarget<T>,
		val options: LinkOptions<T>
) : Comparable<Link<*>> {
	override fun compareTo(other: Link<*>) = id.compareTo(other.id)

	val id = linkId.getAndIncrement()

	init {
		list.add(this)
	}

	private val _eventCount = AtomicLong(0)
	val eventCount get() = _eventCount.get()

	fun record() {
		if (_eventCount.incrementAndGet() > options.eventLimit)
			dispose()
	}

	fun dispose() {
		source.cancel(this)
		list.remove(this)
	}

	override fun toString() = "$id: ${source.view()} -> ${target.view()}"

	companion object {
		private val linkId = AtomicLong(0)
		private val list = ConcurrentSkipListSet<Link<*>>()

		fun view() = list.toList()
	}
}
