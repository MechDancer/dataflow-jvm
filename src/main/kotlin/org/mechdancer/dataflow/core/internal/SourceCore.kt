package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.annotations.ThreadSafety
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * 源节点的通用内核
 * 提供事件管理和基于散列的事件缓存
 * @param size 缓存容量（超过则丢弃最旧的）
 */
@ThreadSafety
internal class SourceCore<T>(private val size: Int) {
    private data class Holder<T>(val value: T)

    private val Holder<T>?.format
        get() = this?.let { x -> true to x.value } ?: false to null

    /** 原子长整型，用于生成事件的唯一Id */
    private val id = AtomicLong(0)

    /** 事件堆 */
    private val buffer = ConcurrentHashMap<Long, Holder<T>>()

    /** 缓存存量 */
    val bufferCount get() = buffer.size

    /** 将一个事件放入堆 */
    fun offer(event: T): Long {
        val newId = id.incrementAndGet()
        buffer[newId] = Holder(event)
        synchronized(buffer) {
            while (buffer.size > size) buffer.remove(buffer.keys.min())
        }
        return newId
    }

    /** 从堆中获取一个事件 */
    operator fun get(id: Long) = buffer[id].format

    /** 从堆中获取第一个事件 */
    tailrec fun get(): Pair<Boolean, T?> =
        if (buffer.isNotEmpty()) buffer[buffer.keys.min()]?.format ?: get()
        else null.format

    /** 从堆中消费一个事件 */
    fun consume(id: Long) = buffer.remove(id).format

    /** 从堆中消费第一个事件 */
    tailrec fun consume(): Pair<Boolean, T?> =
        if (buffer.isNotEmpty()) buffer[buffer.keys.min()]?.format ?: consume()
        else null.format

    /** 从堆中丢弃所有事件 */
    fun clear() = buffer.clear()
}
