package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.DefaultSource
import org.mechdancer.dataflow.core.IBridgeBlock
import org.mechdancer.dataflow.core.IEgress
import org.mechdancer.dataflow.core.IReceivable
import org.mechdancer.dataflow.core.internal.*
import java.util.*
import java.util.concurrent.TimeUnit

class DelayBlock<T>(
    override val name: String = "delay",
    delay: Long,
    unit: TimeUnit)
    : IBridgeBlock<T>, IReceivable<T> {
    override val uuid = UUID.randomUUID()!!
    override val defaultSource by lazy { DefaultSource(this) }

    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<T>(Int.MAX_VALUE)
    private val targetCore = TargetCore<T> { event ->
        scheduler.schedule({
            sourceCore.offer(event).let { newId ->
                Link[this]
                    .filter { it.options.predicate(event) }
                    .forEach { it.offer(newId) }
            }
            receiveCore.call()
        }, delay, unit)
    }

    override fun offer(id: Long, egress: IEgress<T>) = targetCore.offer(id, egress)
    override fun consume(id: Long) = sourceCore consume id
    override fun receive() = receiveCore consumeFrom sourceCore
}
