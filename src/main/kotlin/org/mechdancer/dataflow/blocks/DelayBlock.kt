package org.mechdancer.dataflow.blocks

import kotlinx.coroutines.runBlocking
import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.IPostable.DefaultSource
import org.mechdancer.dataflow.core.internal.*
import java.util.concurrent.TimeUnit

/**
 * 延时模块
 * 接收到消息后延迟指定的时间再发射
 */
class DelayBlock<T>(
    override val name: String = "delay",
    delay: Long,
    unit: TimeUnit
) : IBridgeBlock<T>, IReceivable<T>, IPostable<T> {
    private val linkManager = LinkManager(this)
    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<T>(Int.MAX_VALUE)
    private val targetCore = TargetCore<T> { event ->
        scheduler.schedule(
            {
                runBlocking { linkManager.offer(sourceCore.offer(event), event) }
                receiveCore.call()
            },
            delay, unit
        )
    }

    override val uuid = randomUUID()
    override val defaultSource by lazy { DefaultSource(this) }
    override val targets get() = linkManager.targets

    override suspend fun offer(id: Long, egress: IEgress<T>) = targetCore.offer(id, egress)
    override fun consume(id: Long) = sourceCore consume id
    override fun receive() = receiveCore consumeFrom sourceCore
    override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) =
        linkManager.linkTo(target, options)

    override fun toString() = view()
}
