package org.mechdancer.dataflow.core.internal

import kotlinx.coroutines.*
import org.mechdancer.common.extension.Optional
import org.mechdancer.common.extension.check
import org.mechdancer.common.extension.toOptional
import org.mechdancer.dataflow.core.intefaces.IEgress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock
import kotlin.math.max

/**
 * Common core for source blocks
 * 源节点的通用内核
 *
 * Provides message management and hash-based message caching.
 * 提供消息管理和基于散列的消息缓存
 *
 * @param size cache capacity
 *             缓存容量（超过则丢弃最旧的）
 */
internal class SourceCore<T>(private val size: Int) : IEgress<T> {

    // 原子长整型，用于生成消息的唯一 id
    private val lastId = AtomicLong(0)

    // 消息缓存
    private val buffer: ConcurrentHashMap<Long, Optional<T>> =
        when {
            size <= 0  -> throw IllegalArgumentException("size must be greater than 0")
            size <= 16 -> ConcurrentHashMap(16)
            else       -> ConcurrentHashMap(1)
        }

    // 消费计数
    private val removeCount = AtomicLong(0)

    // 缓存清理周期
    private val cleanPeriod = max(16, size)

    // 缓存清理锁
    private val lock = ReentrantLock()

    /**
     * Cached size
     *
     * 缓存存量
     */
    val bufferSize get() = lastId.get() - removeCount.get()

    /**
     * cache a [msg]
     *
     * 缓存一则消息
     */
    fun offer(msg: T): Long {
        val newId = lastId.getAndIncrement()
        buffer[newId] = msg.toOptional()

        // 每到周期就触发一次清理
        if (newId > 0 && newId % cleanPeriod == 0L)
            lock.withTryLock {
                while (bufferSize > size) {
                    val id = buffer.keys.firstOrNull() ?: break
                    if (buffer.remove(id) != null) removeCount.incrementAndGet()
                }
            }
        return newId
    }

    /**
     * Gets a specific buffered message marked [id]
     *
     * 获取一个缓存的消息
     */
    operator fun get(id: Long): Optional<T> =
        buffer[id]
        ?: Optional.otherwise()

    /**
     * Gets the first buffered message
     *
     * 获取第一个缓存的消息
     */
    fun get(): Optional<T> =
        buffer.values.firstOrNull()
        ?: Optional.otherwise()

    /**
     * Consumes a specific buffered message marked [id]
     *
     * 消费一个缓存的消息
     */
    override fun consume(id: Long): Optional<T> =
        buffer.remove(id)
            ?.also { removeCount.incrementAndGet() }
        ?: Optional.otherwise()

    /**
     * Consumes the first buffered message
     *
     * 消费缓冲中的第一个消息
     */
    fun consume(): Optional<T> {
        while (true) {
            val id = buffer.keys.firstOrNull() ?: return Optional.otherwise()
            return buffer.remove(id)?.also { removeCount.incrementAndGet() } ?: continue
        }
    }

    /**
     * Drop all the buffered messages
     *
     * 丢弃所有缓冲的消息
     */
    fun clear() = buffer.clear()
}
