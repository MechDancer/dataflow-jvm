package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.annotations.ThreadSafety
import org.mechdancer.dataflow.core.IEgress
import org.mechdancer.dataflow.core.Message
import org.mechdancer.dataflow.core.message
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
    private data class Holder<T>(val value: T)

    private val Holder<T>?.pack
        get() = this?.let { x -> message(x.value) } ?: message()

    /** 原子长整型，用于生成事件的唯一Id */
    private val id = AtomicLong(0)

    /** 事件堆 */
    private val buffer = ConcurrentHashMap<Long, Holder<T>>()

    /** 缓存存量 */
    val bufferCount get() = buffer.size

    /** 将一个事件放入堆 */
    fun offer(event: T): Long {
        val newId = id.getAndIncrement()
        buffer[newId] = Holder(event)
        synchronized(buffer) {
            while (buffer.size > size) buffer.remove(buffer.keys.min())
        }
        return newId
    }

    /** 从堆中获取一个事件 */
    operator fun get(id: Long) = buffer[id].pack

    /** 从堆中获取第一个事件 */
    tailrec fun get(): Message<out T> =
        if (buffer.isNotEmpty()) buffer[buffer.keys.min()]?.pack ?: get()
        else null.pack

    /** 从堆中消费一个事件 */
    override infix fun consume(id: Long) = buffer.remove(id).pack

    /** 从堆中消费第一个事件 */
    tailrec fun consume(): Message<out T> =
        if (buffer.isNotEmpty()) buffer[buffer.keys.min()]?.pack ?: consume()
        else null.pack

    /** 从堆中丢弃所有事件 */
    fun clear() = buffer.clear()
}
