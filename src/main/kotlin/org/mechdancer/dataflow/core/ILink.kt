package org.mechdancer.dataflow.core

import java.util.concurrent.ConcurrentSkipListSet

interface ILink<T> : IWithUUID, IEgress<T> {
	val source: ISource<T>
	val target: ITarget<T>
	val options: LinkOptions<T>

	val count: Int
	val rest: Int

	infix fun offer(id: Long): Feedback

	fun dispose()

	companion object {
		/** 全局链接列表 */
		internal val list = ConcurrentSkipListSet<ILink<*>>()

		/** 拓扑改变事件 */
		val changed = broadcast<List<ILink<*>>>("LinkInfo")

		/** 查看全部拓扑 */
		fun all() = list.toList()

		/** 按源从列表中查找 */
		operator fun <T> get(source: ISource<T>) =
			@Suppress("UNCHECKED_CAST")
			list.asSequence().filter { link -> link.source === source }.map { it as ILink<T> }.toList()
	}
}
