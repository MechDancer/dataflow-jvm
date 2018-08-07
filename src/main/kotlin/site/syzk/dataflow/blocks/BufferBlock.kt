package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.*

class BufferBlock<T> : ITarget<T>, ISource<T>, IReceivable<T> {
    override val defaultSource = DefaultSource<T>()

    private val receiveLock = Object()
    private val sourceCore = SourceCore(this)
    private val targetCore = TargetCore<T> { event ->
        val newId = sourceCore.offer(event)
        synchronized(sourceCore.targets) {
            sourceCore.targets
                    .map { it.offer(newId, this) }
                    .any { it.positive }
                    .otherwise {
                        synchronized(receiveLock) {
                            receiveLock.notifyAll()
                        }
                    }
        }
    }

    val count get() = sourceCore.bufferCount

    override fun offer(eventId: Long, source: ISource<T>) = targetCore.offer(eventId, source)
    override fun consume(id: Long) = sourceCore.consume(id)

    override fun linkTo(target: ITarget<T>) = sourceCore.linkTo(target)
    override fun unlink(target: ITarget<T>) = sourceCore.unlink(target)

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
