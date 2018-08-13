package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.core.ISource
import org.mechdancer.dataflow.core.ITarget
import org.mechdancer.dataflow.core.Link
import org.mechdancer.dataflow.core.LinkOptions

@org.mechdancer.dataflow.annotations.ThreadSafe
internal class LinkManager<T>(private val owner: ISource<T>) {
	private val _links = mutableListOf<Link<T>>()
	val links get() = _links.toList()

	fun build(target: ITarget<T>, options: LinkOptions<T>): Link<T> =
			Link(owner, target, options).also {
				synchronized(_links) { _links.add(it) }
			}

	fun cancel(link: Link<T>) {
		synchronized(_links) { _links.remove(link) }
	}
}
