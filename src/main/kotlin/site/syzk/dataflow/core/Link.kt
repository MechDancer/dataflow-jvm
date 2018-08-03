package site.syzk.dataflow.core

import site.syzk.dataflow.core.Feedback.Accept
import site.syzk.dataflow.core.Feedback.Decline
import site.syzk.dataflow.core.annotations.ThreadSafety
import java.util.concurrent.atomic.AtomicInteger

/**
 * LinkTo产生的链接对象
 */
@ThreadSafety(true)
class Link<TIn, TOut>(
        private val target: ITarget<TOut>,
        private val options: LinkOptions<TIn, TOut>
) {
    private var count: AtomicInteger = AtomicInteger(0)

    /**
     * 向目标节点发送
     */
    internal fun post(event: Event<TIn>): Feedback {
        //直接拒绝
        if (options.filter == null || !options.filter.invoke(event.value))
            return Decline
        //向目标节点发送转换过的数据
        val feedback = target.post(Event(
                event.begin,
                options.transformer.invoke(event.value)))
        //若目标节点接受，计数，并检查是否解除链接
        if (feedback == Accept
                && options.counter != null
                && options.counter.invoke(count.incrementAndGet()))
            dispose()
        //返回目标节点的反馈
        return feedback
    }

    /**
     * 解除链接
     */
    fun dispose(): Unit = TODO()
}
