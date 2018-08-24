package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.DefaultSource
import org.mechdancer.dataflow.core.IPropagatorBlock
import org.mechdancer.dataflow.core.IReceivable
import org.mechdancer.dataflow.core.Link
import org.mechdancer.dataflow.core.internal.ReceiveCore
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.TargetCore
import java.util.*

/**
 * 缓冲模块
 * 未消耗的数据将保留，直到被消费
 */
class BufferBlock<T>(
    override val name: String = "buffer",
    size: Int = Int.MAX_VALUE)
    : IPropagatorBlock<T, T>, IReceivable<T> {
    override val uuid = UUID.randomUUID()!!
    override val defaultSource by lazy { DefaultSource(this) }

    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<T>(size)
    private val targetCore = TargetCore<T> { event ->
        sourceCore.offer(event).let { newId ->
            Link[this]
                .filter { it.options.predicate(event) }
                .forEach { it.offer(newId) }
        }
        receiveCore.call()
    }

    val count get() = sourceCore.bufferCount
    fun clear() = sourceCore.clear()

    override fun offer(id: Long, link: Link<T>) = targetCore.offer(id, link)
    override fun consume(id: Long) = sourceCore.consume(id)
    override fun receive() = receiveCore consumeFrom sourceCore
}
