package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.BlockBase
import org.mechdancer.dataflow.core.intefaces.IBlock
import org.mechdancer.dataflow.core.intefaces.IEgress
import org.mechdancer.dataflow.core.intefaces.IFullyBlock
import org.mechdancer.dataflow.core.intefaces.IPostable.DefaultSource
import org.mechdancer.dataflow.core.intefaces.ITarget
import org.mechdancer.dataflow.core.internal.*
import org.mechdancer.dataflow.core.options.ExecutableOptions
import org.mechdancer.dataflow.core.options.LinkOptions

/**
 * 转换模块
 *
 * @param map 转换函数
 */
class TransformBlock<TIn, TOut>(
    name: String = "transform",
    options: ExecutableOptions = ExecutableOptions(),
    private val map: suspend (TIn) -> TOut
) : IFullyBlock<TIn, TOut>, IBlock by BlockBase(name) {
    private val linkManager = LinkManager(this)
    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<TOut>(Int.MAX_VALUE)
    private val targetCore = TargetCore<TIn>(options)
    { event ->
        val out = map(event)
        val newId = sourceCore.offer(out)
        val valuable = linkManager.offer(newId, out).any { it.positive }
        receiveCore.call()
        if (!valuable) sourceCore consume newId
    }

    override val defaultSource by lazy { DefaultSource(this) }
    override val targets get() = linkManager.targets

    override fun offer(id: Long, egress: IEgress<TIn>) = targetCore.offer(id, egress)
    override fun consume(id: Long) = sourceCore consume id
    override fun receive() = receiveCore consumeFrom sourceCore
    override fun linkTo(target: ITarget<TOut>, options: LinkOptions<TOut>) =
        linkManager.linkTo(target, options)
}
