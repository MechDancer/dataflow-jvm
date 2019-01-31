package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.BlockBase
import org.mechdancer.dataflow.core.LinkOptions
import org.mechdancer.dataflow.core.intefaces.IBlock
import org.mechdancer.dataflow.core.intefaces.IEgress
import org.mechdancer.dataflow.core.intefaces.IFullyBlock
import org.mechdancer.dataflow.core.intefaces.IPostable.DefaultSource
import org.mechdancer.dataflow.core.intefaces.ITarget
import org.mechdancer.dataflow.core.internal.*
import org.mechdancer.dataflow.core.message

/**
 * 广播块
 */
class BroadcastBlock<T>(
    name: String = "broadcast",
    private val clone: ((T) -> T)? = null
) : IFullyBlock<T, T>, IBlock by BlockBase(name) {
    private val linkManager = LinkManager(this)
    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<T>(1)
    private val targetCore = TargetCore<T> { event ->
        linkManager.offer(sourceCore.offer(event), event)
        receiveCore.call()
    }

    override val defaultSource by lazy { DefaultSource(this) }
    override val targets get() = linkManager.targets

    override fun offer(id: Long, egress: IEgress<T>) = targetCore.offer(id, egress)
    override fun receive() = receiveCore getFrom sourceCore
    override fun consume(id: Long) =
        sourceCore[id].let {
            if (it.hasValue && clone != null) message(clone.invoke(it.value))
            else it
        }

    override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) =
        linkManager.linkTo(target, options)
}
