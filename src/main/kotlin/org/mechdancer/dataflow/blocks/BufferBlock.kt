package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.IPostable.DefaultSource
import org.mechdancer.dataflow.core.internal.*

/**
 * 缓冲模块
 * 未消耗的数据将保留，直到被消费
 */
class BufferBlock<T>(
    override val name: String = "buffer",
    size: Int = Int.MAX_VALUE
) : IPropagatorBlock<T, T>, IReceivable<T>, IPostable<T> {
    private val linkManager = LinkManager(this)
    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<T>(size)
    private val targetCore = TargetCore<T> { event ->
        linkManager.offer(sourceCore.offer(event), event)
        receiveCore.call()
    }

    override val uuid = randomUUID()
    override val defaultSource by lazy { DefaultSource(this) }
    override val targets get() = linkManager.targets

    /** @return 缓存存量 */
    val count get() = sourceCore.bufferCount

    /** 清空缓存 */
    fun clear() = sourceCore.clear()

    override suspend fun offer(id: Long, egress: IEgress<T>) = targetCore.offer(id, egress)
    override fun consume(id: Long) = sourceCore consume id
    override fun receive() = receiveCore consumeFrom sourceCore
    override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) =
        linkManager.linkTo(target, options)
}
