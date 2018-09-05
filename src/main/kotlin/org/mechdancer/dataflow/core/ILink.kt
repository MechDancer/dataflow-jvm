package org.mechdancer.dataflow.core

import java.util.concurrent.ConcurrentSkipListSet

/**
 * 链接的定义
 */
interface ILink<T> : IWithUUID, IEgress<T> {
	val source: ISource<T>
	val target: ITarget<T>
	val options: LinkOptions<T>

	val count: Int
	val rest: Int

	/**
	 * 通知事件到来
	 * 必然由源来通知，不必再指定源，也不接受其他源的消息
	 */
	infix fun offer(id: Long): Feedback

	fun dispose()

	companion object {
		/** 全局链接列表 */
		internal val list = ConcurrentSkipListSet<ILink<*>>()

		/** 拓扑改变事件 */
		val changed = broadcast<List<ILink<*>>>("LinkInfo")

		/** 查看全部拓扑 */
		fun all() = list.toList()
	}
}
