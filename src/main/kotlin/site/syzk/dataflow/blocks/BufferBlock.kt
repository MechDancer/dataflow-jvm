package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.post
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class BufferBlock<T> : ISource<T>, ITarget<T> {
    private val queue = LinkedBlockingQueue<T>()
    private val initList = mutableListOf<ITarget<T>>()

    init {
        thread {
            while (true) {
                while (initList.isEmpty());
                while (initList.isNotEmpty())
                    initList.first().post(queue.take())
            }
        }
    }

    override fun linkTo(target: ITarget<T>) {
        initList.add(target)
    }

    override fun consume(event: T) {
        queue.put(event)
    }
}
