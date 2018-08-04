package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.post
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class BufferBlock<T> : ISource<T>, ITarget<T> {
    private val queue = LinkedBlockingQueue<T>()

    override fun receive(timeout: Long) = queue.poll()

    override fun linkTo(target: ITarget<T>) {
        thread { while (true) target.post(queue.poll()) }
    }

    override fun consume(event: T) = queue.put(event)
}
