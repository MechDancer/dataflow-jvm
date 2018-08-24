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
 * 广播节点
 * 堆中的事件只会被新事件顶替，不会因为接收而消耗
 */
class BroadcastBlock<T>(
    override val name: String = "broadcast",
    private val clone: ((T?) -> T)? = null)
    : IPropagatorBlock<T, T>, IReceivable<T> {
    override val uuid = UUID.randomUUID()!!
    override val defaultSource by lazy { DefaultSource(this) }

    //--------------------------
    // IReceivable
    //--------------------------
    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<T>(1)
    private val targetCore = TargetCore<T> { event ->
        sourceCore.offer(event).let { newId ->
            Link[this]
                .filter { it.options.predicate(event) }
                .forEach { it.offer(newId) }
        }
        receiveCore.call()
    }

    override fun offer(id: Long, link: Link<T>) = targetCore.offer(id, link)
    override fun consume(id: Long): Pair<Boolean, T?> = sourceCore[id]
    override fun receive() = receiveCore getFrom sourceCore
}
