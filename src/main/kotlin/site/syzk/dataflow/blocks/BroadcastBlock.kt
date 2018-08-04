package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.post
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class BroadcastBlock<T> : ISource<T>, ITarget<T> {
    private val buffer = LinkedBlockingQueue<T>()
    private val links = mutableListOf<ITarget<T>>()

    init {
        thread {
            while (true) {
                val temp = buffer.take()
                links.forEach { it.post(temp) }
            }
        }
    }

    override fun consume(event: T) {
        if (!buffer.isEmpty()) buffer.take()
        buffer.put(event)
    }

    override fun receive(timeout: Long) = buffer.take()

    override fun linkTo(target: ITarget<T>) {
        links.add(target)
    }
}
