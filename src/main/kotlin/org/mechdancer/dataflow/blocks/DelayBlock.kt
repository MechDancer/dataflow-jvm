package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.DefaultSource
import org.mechdancer.dataflow.core.IBridgeBlock
import org.mechdancer.dataflow.core.IReceivable
import org.mechdancer.dataflow.core.Link
import org.mechdancer.dataflow.core.internal.ReceiveCore
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.TargetCore
import org.mechdancer.dataflow.core.internal.scheduler
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

    override fun offer(id: Long, link: Link<T>) = targetCore.offer(id, link)
    override fun consume(id: Long) = sourceCore consume id
    override fun receive() = receiveCore consumeFrom sourceCore
}
