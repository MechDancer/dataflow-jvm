package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.TargetCore
import org.mechdancer.dataflow.core.internal.otherwise
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet

/**
 * 转换模块
 * @param map 转换函数
 */
class TransformBlock<TIn, TOut>(
		override val name: String = "transform",
		options: ExecutableOptions = executableOptions(),
		private val map: (TIn) -> TOut
) : IPropagatorBlock<TIn, TOut>, IReceivable<TOut> {
	override val uuid = UUID.randomUUID()!!
	override val defaultSource = DefaultSource(this)
	private val links = ConcurrentSkipListSet<Link<TOut>>()

	//--------------------------
	// ITarget & ISource
	//--------------------------

	private val sourceCore = SourceCore<TOut>()
	private val targetCore = TargetCore<TIn>(options)
	{ event ->
		val out = map(event)
		val newId = sourceCore.offer(out)
		@Suppress("UNCHECKED_CAST")
		links.filter { it.options.predicate(out) }
				.any { it.target.offer(newId, it).positive }
				.otherwise {
					sourceCore.drop(newId)
					synchronized(receiveLock) {
						receivable = true
						value = out
						receiveLock.notifyAll()
					}
				}
	}

	//--------------------------
	// IReceivable
	//--------------------------

	private val receiveLock = Object()
	private var receivable = false
	private var value: TOut? = null

	//--------------------------
	// Methods
	//--------------------------

	override fun offer(id: Long, link: Link<TIn>) = targetCore.offer(id, link)
	override fun consume(id: Long, link: Link<TOut>) =
			sourceCore.consume(id).apply { if (this.first) link.record() }

	override fun linkTo(target: ITarget<TOut>, options: LinkOptions<TOut>) =
			Link(this, target, options).apply {
				links.add(this)
			}

	override fun cancel(link: Link<TOut>) = links.remove(link)

	override fun receive(): TOut =
			synchronized(receiveLock) {
				while (!receivable) receiveLock.wait()
				receivable = false
				@Suppress("UNCHECKED_CAST")
				value as TOut
			}
}
