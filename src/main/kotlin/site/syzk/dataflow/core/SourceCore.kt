package site.syzk.dataflow.core

import java.util.concurrent.atomic.AtomicLong

/**
 * 源节点的通用内核
 */
internal class SourceCore<T> {
    /**
     * 原子长整型，用于生成唯一Id
     */
    private val id = AtomicLong(0)

    /**
     * 事件堆
     */
    private val buffer = hashMapOf<Long, T>()

    /**
     * 将一个事件放入堆
     */
    fun register(event: T): Long {
        val newId = id.incrementAndGet()
        buffer[newId] = event
        return newId
    }

    /**
     * 从堆中消费一个事件
     */
    fun consume(id: Long) = buffer.containsKey(id) to buffer.remove(id)
}
