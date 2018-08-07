package site.syzk.dataflow.core

import site.syzk.dataflow.annotations.ThreadSafe
import java.util.concurrent.atomic.AtomicLong

/**
 * 源节点的通用内核
 */
@ThreadSafe(true)
internal class SourceCore<T>(private val owner: ISource<T>) {
    /**
     * 原子长整型，用于生成唯一Id
     */
    private val id = AtomicLong(0)

    /**
     * 事件堆
     */
    private val buffer = hashMapOf<Long, T>()

    val bufferCount get() = buffer.size

    /**
     * 将一个事件放入堆
     */
    fun offer(event: T): Long {
        val newId = id.incrementAndGet()
        buffer[newId] = event
        return newId
    }

    /**
     * 从堆中消费一个事件
     */
    fun consume(id: Long): Pair<Boolean, T?> {
        synchronized(buffer) {
            return buffer.containsKey(id) to buffer.remove(id)
        }
    }

    /**
     * 从堆中消费第一个事件
     */
    fun consumeFirst(): Pair<Boolean, T?> {
        synchronized(buffer) {
            return if (!buffer.any())
                false to null
            else
                buffer.keys.min().let { buffer.containsKey(it) to buffer.remove(it) }
        }
    }

    /**
     * 从堆中丢弃一个事件
     */
    fun drop(id: Long) {
        synchronized(buffer) { buffer.remove(id) }
    }

    /**
     * 宿节点
     */
    val targets = mutableListOf<ITarget<T>>()

    /**
     * 添加链接
     */
    fun linkTo(target: ITarget<T>): Link<T> {
        synchronized(target) { targets.add(target) }
        return Link(owner, target)
    }

    /**
     * 取消链接
     */
    fun unlink(target: ITarget<T>) {
        synchronized(target) { targets.remove(target) }
    }
}
