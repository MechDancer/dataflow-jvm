package org.mechdancer.dataflow.core.internal

import org.mechdancer.common.extension.Optional
import org.mechdancer.common.extension.toOptional
import org.mechdancer.dataflow.annotations.ThreadSafety
import org.mechdancer.dataflow.core.intefaces.IEgress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * 源节点的通用内核
 *
 * 提供事件管理和基于散列的事件缓存
 * @param size 缓存容量（超过则丢弃最旧的）
 */
@ThreadSafety
internal class SourceCore<T>(private val size: Int) : IEgress<T> {
    /** 原子长整型，用于生成事件的唯一Id */
    private val id = AtomicLong(0)

    /** 事件堆 */
    private val buffer = ConcurrentHashMap<Long, Optional<T>>()

    /** 缓存存量 */
    val bufferCount get() = buffer.size

    /** 将一个事件放入堆 */
    fun offer(event: T): Long {
        val newId = id.getAndIncrement()
        buffer[newId] = event.toOptional()
        synchronized(buffer) {
            while (buffer.size > size) buffer.remove(buffer.keys.min())
        }
        return newId
    }

    /** 从堆中获取一个事件 */
    operator fun get(id: Long): Optional<T> =
        buffer[id] ?: Optional.otherwise()

    /** 从堆中获取第一个事件 */
    fun get(): Optional<T> =
        buffer.values.firstOrNull() ?: Optional.otherwise()

    /** 从堆中消费一个事件 */
    override infix fun consume(id: Long): Optional<T> =
        buffer.remove(id) ?: Optional.otherwise()

    /** 从堆中消费第一个事件 */
    fun consume(): Optional<T> {
        while (true) {
            val id = buffer.keys.min() ?: return Optional.otherwise<T>()
            return buffer.remove(id) ?: continue
        }
    }

    /** 从堆中丢弃所有事件 */
    fun clear() = buffer.clear()
}
