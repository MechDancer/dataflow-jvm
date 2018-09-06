package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.core.ILink
import org.mechdancer.dataflow.core.ISource
import org.mechdancer.dataflow.core.ITarget
import org.mechdancer.dataflow.core.LinkOptions
import java.util.concurrent.ConcurrentSkipListSet

internal class LinkManager<T>(private val owner: ISource<T>) {
	private val list = ConcurrentSkipListSet<ILink<T>>()

	val targets get() = list.map { it.target }.distinct()

	fun linkTo(target: ITarget<T>, options: LinkOptions<T>): ILink<T> =
		Link(owner, target, options, this).also { list += it }

	fun remove(link: ILink<T>) {
		list -= link
	}

	operator fun get(id: Long, value: T) =
		list.filter { it.options.predicate(value) }
			.map { it.offer(id) }
}
