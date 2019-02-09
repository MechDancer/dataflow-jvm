package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.blocks.TargetType.Broadcast
import org.mechdancer.dataflow.blocks.TargetType.Cold
import org.mechdancer.dataflow.core.BlockBase
import org.mechdancer.dataflow.core.Feedback
import org.mechdancer.dataflow.core.intefaces.IBlock
import org.mechdancer.dataflow.core.intefaces.IEgress
import org.mechdancer.dataflow.core.intefaces.IFullyBlock
import org.mechdancer.dataflow.core.intefaces.IPostable.DefaultSource
import org.mechdancer.dataflow.core.intefaces.ITarget
import org.mechdancer.dataflow.core.internal.*
import org.mechdancer.dataflow.core.options.ExecutableOptions
import org.mechdancer.dataflow.core.options.LinkOptions

class StandardBlock<TIn, TOut>(
    name: String,
    bufferSize: Int,
    private val targetType: TargetType,
    options: ExecutableOptions,
    private val map: suspend (TIn) -> TOut
) : IFullyBlock<TIn, TOut>, IBlock by BlockBase(name) {
    private val linkManager = LinkManager(this)
    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<TOut>(bufferSize)
    private val targetCore = TargetCore<TIn>(options)
    { event ->
        val out = map(event)
        val id = sourceCore.offer(out)
        val significant = linkManager.offer(id, out).none(Feedback::positive)
        receiveCore.call()
        if (targetType == Cold && significant)
            sourceCore consume id
    }

    override val defaultSource by lazy { DefaultSource(this) }
    override val targets get() = linkManager.targets

    val bufferSize get() = sourceCore.bufferCount
    fun clear() = sourceCore.clear()

    override fun offer(id: Long, egress: IEgress<TIn>) =
        targetCore.offer(id, egress)

    override fun consume(id: Long) =
        if (targetType == Broadcast) sourceCore[id]
        else sourceCore consume id

    override fun receive() =
        if (targetType == Broadcast) receiveCore getFrom sourceCore
        else receiveCore consumeFrom sourceCore

    override fun linkTo(target: ITarget<TOut>, options: LinkOptions<TOut>) =
        linkManager.linkTo(target, options)
}
