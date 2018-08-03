package site.syzk.dataflow.core

import site.syzk.dataflow.core.Feedback.Accept
import site.syzk.dataflow.core.Feedback.Decline
import java.util.concurrent.atomic.AtomicInteger

@ThreadSafety(true)
class Link<TIn, TOut>(
		private val target: ITarget<TOut>,
		private val filter: (TIn) -> Boolean,
		private val transformer: (TIn) -> TOut,
		private val counter: (Int) -> Boolean
) {
    private var count: AtomicInteger = AtomicInteger(0)

    /**
     * 向目标节点发送
     */
    internal fun post(event: Event<TIn>): Feedback {
        //直接拒绝
        if (!filter(event.value)) return Decline
        //进行转换
        val actual = Event(event.begin, transformer(event.value))
        //向目标节点发送
        val feedback = target.post(actual)
        //若目标节点接受，计数，并检查是否解除链接
        if (feedback == Accept && counter(count.incrementAndGet()))
            dispose()
        //返回目标节点的反馈
        return feedback
    }

    /**
     * 解除链接
     */
    fun dispose(): Unit = TODO()
}
