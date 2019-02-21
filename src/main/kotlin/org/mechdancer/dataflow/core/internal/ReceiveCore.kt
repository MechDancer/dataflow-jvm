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

    @Suppress("UNCHECKED_CAST")
    fun call() {
        for (i in 0 until waitList.size) {
            waitList
                .poll()
                ?.also { (con, block) ->
                    val temp: Any? = block() //TODO: unboxed data mistakenly, which can be `null`
                    if (temp == null) con.resume(null as T)
                    else (temp as? T)
                             ?.toOptional()
                             ?.then { con.resume(it) }
                             ?.getOrNull()
                         ?: waitList.add(con to block)
                }
            ?: break
        }
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
