package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.post
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class BufferBlock<T> : ISource<T>, ITarget<T> {
    private val queue = LinkedBlockingQueue<Pair<T, Queue<ITarget<T>>>>()
    private val initList = mutableListOf<ITarget<T>>()

    init {
        thread {
            while (true) {
                val current = queue.take()
                while (current.second.isEmpty()) {
                }
                while (current.second.isNotEmpty()) {
                    current.second.first().post(current.first)
                }
            }
        }
    }

    override fun linkTo(target: ITarget<T>) {
        initList.add(target)
        queue.forEach { it.second.offer(target) }
    }

    override fun consume(event: T) {
        queue.put(event to LinkedList(initList))
    }
}
