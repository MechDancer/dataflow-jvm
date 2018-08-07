package site.syzk.dataflow.core.internal

import site.syzk.dataflow.core.Feedback.Accepted
import site.syzk.dataflow.core.Feedback.NotAvailable
import site.syzk.dataflow.core.ISource
import kotlin.concurrent.thread

/**
 * 目的节点的通用内核
 * @param action 目的节点接收事件后的动作
 */
internal class TargetCore<T>(private val action: (T) -> Unit) {
    fun offer(eventId: Long, source: ISource<T>) =
            source.consume(eventId)
                    .let {
                        if (it.first) {
                            @Suppress("UNCHECKED_CAST")
                            thread { action(it.second as T) }
                            Accepted
                        } else
                            NotAvailable
                    }
}
