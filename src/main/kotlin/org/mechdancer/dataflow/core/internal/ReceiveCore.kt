package org.mechdancer.dataflow.core.internal

import org.mechdancer.common.extension.Optional
import java.util.concurrent.ConcurrentSkipListMap
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 接收模块内核
 */
internal class ReceiveCore {

    fun call() {
        waitList.forEach { (con, block) ->
            con.resume(block())
        }
    }

    private val waitList = ConcurrentSkipListMap<Continuation<Any?>, () -> Optional<*>>()

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T> get(block: () -> Optional<T>): T = suspendCoroutine {
        waitList[it as Continuation<Any?>] = block
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
