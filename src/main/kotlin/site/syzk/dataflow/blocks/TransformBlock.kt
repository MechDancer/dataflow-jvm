package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.post
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class TransformBlock<TIn, TOut>(private val map: (TIn) -> TOut)
    : ITarget<TIn>, ISource<TOut> {
    private val buffer = LinkedBlockingQueue<TOut>()

    override fun consume(event: TIn) {
        if (!buffer.isEmpty()) buffer.poll()
        buffer.put(map(event))
    }

    override fun receive(timeout: Long) = buffer.poll()

    override fun linkTo(target: ITarget<TOut>) {
        thread { while (true) target.post(buffer.poll()) }
    }
}
