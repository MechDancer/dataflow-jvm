package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.post
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class TransformBlock<TIn, TOut>(private val map: (TIn) -> TOut)
    : ITarget<TIn>, ISource<TOut> {
    private val buffer = LinkedBlockingQueue<TOut>()
    private val links = mutableListOf<ITarget<TOut>>()

    init {
        thread {
            while (true)
                if (links.isNotEmpty()) links.first().post(buffer.take())
        }
    }

    override fun consume(event: TIn) {
        if (!buffer.isEmpty()) buffer.take()
        buffer.put(map(event))
    }

    override fun receive(timeout: Long) = buffer.take()

    override fun linkTo(target: ITarget<TOut>) {
        links.add(target)
    }
}
