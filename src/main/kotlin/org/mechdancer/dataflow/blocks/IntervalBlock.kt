package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.BlockBase
import org.mechdancer.dataflow.core.intefaces.IBlock
import org.mechdancer.dataflow.core.intefaces.IExitBlock
import org.mechdancer.dataflow.core.intefaces.ITarget
import org.mechdancer.dataflow.core.internal.*
import org.mechdancer.dataflow.core.options.LinkOptions
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class IntervalBlock(
    name: String = "interval",
    private val period: Long,
    private val unit: TimeUnit,
    start: Boolean
) : IExitBlock<Long>, IBlock by BlockBase(name) {
    private val linkManager = LinkManager(this)
    private val receiveCore = ReceiveCore<Long>()
    private val sourceCore = SourceCore<Long>(Int.MAX_VALUE)

    private var t = 0L
    private var task: ScheduledFuture<*>? = null

    override val targets get() = linkManager.targets

    init {
        if (start) start()
    }

    /** 启动 */
    fun start() {
        task = scheduler.scheduleAtFixedRate(
            {
                t = sourceCore.offer(t)
                linkManager.offer(sourceCore.offer(t), t)
                receiveCore.call()
            }, 0, period, unit)
    }

    /** 暂停 */
    fun pause() = task?.cancel(false)

    override fun consume(id: Long) = sourceCore consume id
    override suspend fun receive() = receiveCore consumeFrom sourceCore
    override fun linkTo(target: ITarget<Long>, options: LinkOptions<Long>) =
        linkManager.linkTo(target, options)
}
