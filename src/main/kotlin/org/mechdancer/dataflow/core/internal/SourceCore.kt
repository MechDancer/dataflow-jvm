package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.annotations.ThreadSafety
import java.util.concurrent.atomic.AtomicLong

/**
 * 源节点的通用内核
 * 提供事件管理和基于散列的事件缓存
 * @param size 缓存容量（超过则丢弃最旧的）
 */
@ThreadSafety
internal class SourceCore<T>(private val size: Int = Int.MAX_VALUE) {
    /** 原子长整型，用于生成事件的唯一Id */
    private val id = AtomicLong(0)

    /** 事件堆 */
    private val buffer = hashMapOf<Long, T>()

    /** 缓存存量 */
    val bufferCount get() = buffer.size

    /** 将一个事件放入堆 */
    fun offer(event: T): Long {
        val newId = id.incrementAndGet()
        synchronized(buffer) {
            buffer[newId] = event
            while (buffer.size > size) buffer.remove(buffer.keys.min())
        }
        return newId
    }

    /** 从堆中消费一个事件 */
    fun consume(id: Long): Pair<Boolean, T?> {
        synchronized(buffer) {
            return buffer.containsKey(id)
                .zip { buffer.remove(id) }
        }
    }

    /** 从堆中消费第一个事件 */
    fun consume(): Pair<Boolean, T?> {
        synchronized(buffer) {
            return buffer.isNotEmpty()
                .zip { buffer.remove(buffer.keys.min()) }
        }
    }

    /** 从堆中丢弃一个事件 */
    fun drop(id: Long) {
        synchronized(buffer) { buffer.remove(id) }
    }

    /** 从堆中丢弃所有事件 */
    fun clear() {
        synchronized(buffer) { buffer.clear() }
    }
}
