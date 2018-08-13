package org.mechdancer.dataflow.core

fun <TIn, TOut, TMid> encapsulate(
		i: IPropagatorBlock<TIn, TMid>,
		o: IPropagatorBlock<TMid, TOut>) =
		object : IPropagatorBlock<TIn, TOut> {
			init {
				i linkTo o
			}

			override val name = "${i.name} -> ${o.name}"
			override val defaultSource = i.defaultSource

			override fun offer(id: Long, link: Link<TIn>) =
					i.offer(id, link)

			override fun consume(id: Long, link: Link<TOut>) =
					o.consume(id, link)

			override fun linkTo(target: ITarget<TOut>, options: LinkOptions<TOut>) =
					o.linkTo(target, options)

			override fun unlink(link: Link<TOut>) =
					o.unlink(link)
		}
