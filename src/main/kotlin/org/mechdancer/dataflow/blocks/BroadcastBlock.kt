package org.mechdancer.dataflow.blocks

import org.mechdancer.common.extension.toOptional
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
 * 广播块
 */
class BroadcastBlock<T>(
    name: String = "broadcast",
    bufferSize: Int = Int.MAX_VALUE,
    private val clone: ((T) -> T)? = null
) : IFullyBlock<T, T>, IBlock by BlockBase(name) {
    private val linkManager = LinkManager(this)
    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<T>(bufferSize)
    private val targetCore = TargetCore<T>(
        ExecutableOptions(parallelismDegree = 1)
    ) { event ->
        linkManager.offer(sourceCore.offer(event), event)
        receiveCore.call()
    }

    override val defaultSource by lazy { DefaultSource(this) }
    override val targets get() = linkManager.targets

    override fun offer(id: Long, egress: IEgress<T>) =
        targetCore.offer(id, egress)

    override fun receive() =
        receiveCore getFrom sourceCore

    override fun consume(id: Long) =
        if (clone != null)
            sourceCore[id].then { value -> return clone.invoke(value).toOptional() }
        else
            sourceCore[id]

    override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) =
        linkManager.linkTo(target, options)
}
