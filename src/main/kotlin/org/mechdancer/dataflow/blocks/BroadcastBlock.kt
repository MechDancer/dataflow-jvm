package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.IPostable.DefaultSource
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
    private val clone: ((T) -> T)? = null)
    : IPropagatorBlock<T, T>, IReceivable<T>, IPostable<T> {
    override val uuid = UUID.randomUUID()!!
    override val defaultSource by lazy { DefaultSource(this) }

    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<T>(1)
    private val targetCore = TargetCore<T> { event ->
        sourceCore.offer(event).let { newId ->
            ILink[this]
                .filter { it.options.predicate(event) }
                .forEach { it.offer(newId) }
        }
        receiveCore.call()
    }

    override fun offer(id: Long, egress: IEgress<T>) = targetCore.offer(id, egress)
    override fun receive() = receiveCore getFrom sourceCore
    override fun consume(id: Long) = sourceCore[id]
        .let {
            if (it.hasValue && clone != null) message(clone.invoke(it.value))
            else it
        }
}
