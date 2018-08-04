package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.post
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class TransformBlock<TIn, TOut>(private val map: (TIn) -> TOut)
    : ITarget<TIn>, ISource<TOut> {
    private val queue = LinkedBlockingQueue<Pair<TOut, Queue<ITarget<TOut>>>>()
    private val initList = mutableListOf<ITarget<TOut>>()

    init {
        thread {
            while (true) {
                val current = queue.take()
                while (current.second.isNotEmpty()) {
                    current.second.poll()?.post(current.first)
                }
            }
        }
    }

    override fun linkTo(target: ITarget<TOut>) {
        initList.add(target)
        queue.forEach { it.second.offer(target) }
    }

    override fun consume(event: TIn) {
        queue.put(map(event) to LinkedList(initList))
    }
}
