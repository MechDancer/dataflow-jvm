package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.IReceivable
import org.mechdancer.dataflow.core.ISource
import org.mechdancer.dataflow.core.internal.Link
import org.mechdancer.dataflow.core.internal.ReceiveCore
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.scheduler
import java.util.*
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class IntervalBlock(
    override val name: String = "interval",
    private val period: Long,
    private val unit: TimeUnit,
    immediately: Boolean
) : ISource<Long>, IReceivable<Long> {
    override val uuid = UUID.randomUUID()!!

    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<Long>(Int.MAX_VALUE)

    private var t = 0L
    private var task: ScheduledFuture<*>? = null

    init {
        if (immediately) start()
    }

    fun start() {
        task = scheduler.scheduleAtFixedRate({
            t = sourceCore.offer(t)
            Link[this]
                .filter { it.options.predicate(t) }
                .forEach { it.offer(t) }
            receiveCore.call()
        }, 0, period, unit)
    }

    fun pause() = task?.cancel(false)

    override fun consume(id: Long) = sourceCore consume id
    override fun receive() = receiveCore consumeFrom sourceCore
}
