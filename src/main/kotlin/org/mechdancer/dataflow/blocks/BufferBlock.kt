package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.BlockBase
import org.mechdancer.dataflow.core.LinkOptions
import org.mechdancer.dataflow.core.intefaces.IBlock
import org.mechdancer.dataflow.core.intefaces.IEgress
import org.mechdancer.dataflow.core.intefaces.IFullyBlock
import org.mechdancer.dataflow.core.intefaces.IPostable.DefaultSource
import org.mechdancer.dataflow.core.intefaces.ITarget
import org.mechdancer.dataflow.core.internal.*

class BufferBlock<T>(
    name: String = "buffer",
    size: Int = Int.MAX_VALUE
) : IFullyBlock<T, T>, IBlock by BlockBase(name) {
    private val linkManager = LinkManager(this)
    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<T>(size)
    private val targetCore = TargetCore<T> { event ->
        linkManager.offer(sourceCore.offer(event), event)
        receiveCore.call()
    }

    override val defaultSource by lazy { DefaultSource(this) }
    override val targets get() = linkManager.targets

    /** @return 缓存存量 */
    val count get() = sourceCore.bufferCount

    /** 清空缓存 */
    fun clear() = sourceCore.clear()

    override fun offer(id: Long, egress: IEgress<T>) = targetCore.offer(id, egress)
    override fun consume(id: Long) = sourceCore consume id
    override fun receive() = receiveCore consumeFrom sourceCore
    override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) =
        linkManager.linkTo(target, options)
}
