package site.syzk.dataflow.core

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

    fun recordEvent() {
        if (_eventCount.incrementAndGet() > options.eventLimit)
            dispose()
    }

    fun dispose() = source.unlink(target)
}
