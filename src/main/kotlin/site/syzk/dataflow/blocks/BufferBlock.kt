package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.*
import site.syzk.dataflow.core.Feedback.Accepted
import site.syzk.dataflow.core.internal.LinkManager
import site.syzk.dataflow.core.internal.SourceCore
import site.syzk.dataflow.core.internal.TargetCore
import site.syzk.dataflow.core.internal.otherwise

/**
 * 缓冲模块
 * 未消耗的数据将保留，直到被消费
 */
class BufferBlock<T> : ITarget<T>, ISource<T>, IReceivable<T> {
    override val defaultSource = DefaultSource<T>()

    private val manager = LinkManager(this)
    private val receiveLock = Object()
    private val sourceCore = SourceCore<T>()
    private val targetCore = TargetCore<T> { event ->
        val newId = sourceCore.offer(event)
        val result = manager.links
                .filter { it.options.predicate(event) }
                .map { it to it.target.offer(newId, this) }
        result.any { it.second.positive }
                .otherwise {
                    synchronized(receiveLock) {
                        receiveLock.notifyAll()
                    }
                }
        result.filter { it.second == Accepted }
                .forEach { it.first.recordEvent() }
    }

    val count get() = sourceCore.bufferCount

    override fun offer(id: Long, source: ISource<T>) = targetCore.offer(id, source)
    override fun consume(id: Long) = sourceCore.consume(id)

    override fun linkTo(target: ITarget<T>, options: LinkOptions<T>?) =
            manager.linkTo(target, options ?: linkOptions())

    override fun unlink(target: ITarget<T>) =
            manager.unlink(target)

    override fun receive(): T {
        synchronized(receiveLock) {
            var pair = sourceCore.consumeFirst()
            while (!pair.first) {
                receiveLock.wait()
                pair = sourceCore.consumeFirst()
            }
            @Suppress("UNCHECKED_CAST") return pair.second as T
        }
    }
}
