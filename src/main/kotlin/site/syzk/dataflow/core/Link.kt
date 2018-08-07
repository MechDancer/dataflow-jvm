package site.syzk.dataflow.core

import java.util.concurrent.atomic.AtomicLong

/**
 * 链接信息
 * @param source 事件源
 * @param target 事件宿
 */
class Link<T>(
        private val source: ISource<T>,
        val target: ITarget<T>
) {
    private val _eventCount = AtomicLong(0)
    val eventCount get() = _eventCount.get()

    fun recordEvent() {
        _eventCount.incrementAndGet()
    }

    fun dispose() = source.unlink(target)
}
