package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.IPostable.DefaultSource
import org.mechdancer.dataflow.core.internal.ReceiveCore
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.TargetCore
import java.util.*

/**
 * 转换模块
 * @param map 转换函数
 */
class TransformBlock<TIn, TOut>(
	override val name: String = "transform",
	options: ExecutableOptions = ExecutableOptions(),
	private val map: (TIn) -> TOut
) : IPropagatorBlock<TIn, TOut>, IReceivable<TOut>, IPostable<TIn> {
	override val uuid = UUID.randomUUID()!!
	override val defaultSource by lazy { DefaultSource(this) }

	private val receiveCore = ReceiveCore()
	private val sourceCore = SourceCore<TOut>(Int.MAX_VALUE)
	private val targetCore = TargetCore<TIn>(options)
	{ event ->
		val out = map(event)
		val newId = sourceCore.offer(out)
		val valuable = ILink[this]
			.filter { it.options.predicate(out) }
			.any { it.offer(newId).positive }
		receiveCore.call()
		if (!valuable) sourceCore consume newId
	}

	override fun offer(id: Long, egress: IEgress<TIn>) = targetCore.offer(id, egress)
	override fun consume(id: Long) = sourceCore consume id
	override fun receive() = receiveCore consumeFrom sourceCore
}
