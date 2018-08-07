package site.syzk.dataflow.core

import site.syzk.dataflow.core.Feedback.Accepted
import java.util.concurrent.atomic.AtomicLong

/**
 * 链接信息
 * @param source 事件源
 * @param target 事件宿
 */
class Link<T>(
        private val source: ISource<T>,
        val target: ITarget<T>,
        val options: LinkOptions<T>
) {
    private val _eventCount = AtomicLong(0)
    val eventCount get() = _eventCount.get()

    fun offer(id: Long, source: ISource<T>): Feedback {
        val feedback = target.offer(id, source)
        if (feedback == Accepted && _eventCount.incrementAndGet() > options.eventLimit)
            dispose()
        return feedback
    }

    fun dispose() = source.unlink(target)
}
