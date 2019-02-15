package org.mechdancer.dataflow.core.internal

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.mechdancer.common.extension.Optional

/**
 * 接收模块内核
 */
internal class ReceiveCore {
    private val channel = Channel<Unit>(1)

    fun call() = runBlocking { channel.offer(Unit) }

    private suspend inline fun <T> get(block: () -> Optional<T>): T {
        while (true) {
            block().then {
                channel.offer(Unit)
                return it
            }
            channel.receive()
        }
    }

    /**
     * 从 [sourceCore] 消费最新的消息
     */
    suspend infix fun <T> consumeFrom(sourceCore: SourceCore<T>): T =
        get { sourceCore.consume() }

    /**
     * 从 [sourceCore] 获取最新的消息（不消费）
     */
    suspend infix fun <T> getFrom(sourceCore: SourceCore<T>) =
        get { sourceCore.get() }
}
