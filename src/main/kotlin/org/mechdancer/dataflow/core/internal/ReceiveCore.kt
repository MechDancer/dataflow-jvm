package org.mechdancer.dataflow.core.internal

import org.mechdancer.common.extension.Optional
import org.mechdancer.common.extension.toOptional
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 接收模块内核
 */
internal class ReceiveCore<T> {
    private val waitList = ConcurrentLinkedQueue<Pair<Continuation<T>, () -> Optional<T>>>()

    fun call() {
        for (i in 0 until waitList.size)
            waitList
                .poll()
                ?.let { (con, block) ->
                    @Suppress("UNCHECKED_CAST")
                    (block() as? T)
                        ?.toOptional()
                        ?.then { con.resume(it) }
                        ?.otherwise { waitList.add(con to block) }
                }
            ?: break
    }

    /**
     * 从 [sourceCore] 消费最新的消息
     */
    suspend infix fun consumeFrom(sourceCore: SourceCore<T>): T {
        sourceCore.consume().then { return it }
        return suspendCoroutine { waitList.add(it to sourceCore::consume) }
    }

    /**
     * 从 [sourceCore] 获取最新的消息（不消费）
     */
    suspend infix fun getFrom(sourceCore: SourceCore<T>): T {
        sourceCore.get().then { return it }
        return suspendCoroutine { waitList.add(it to sourceCore::get) }
    }
}
