package org.mechdancer.dataflow.core

import java.util.*

/**
 * 封装
 * 此方法将两个块合并成一个，但这样做只是让他们看起来是一个块，没有实际的效果，也不会在两个块之间建立通知的逻辑
 */
fun <TIn, TOut> encapsulate(
		i: ITarget<TIn>,
		o: ISource<TOut>,
		name: String = "") =
		object : IPropagatorBlock<TIn, TOut> {
			override val uuid = UUID.randomUUID()
			override val defaultSource = i.defaultSource
			override val name =
					if (name.isEmpty())
						"${i.name} -> ${o.name}"
					else name

			override fun offer(id: Long, link: Link<TIn>) = i.offer(id, link)
			override fun consume(id: Long) = o.consume(id)

			override fun linkTo(target: ITarget<TOut>, options: LinkOptions<TOut>) =
					o.linkTo(target, options)
		}
