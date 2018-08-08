package site.syzk.dataflow.core.internal

import site.syzk.dataflow.annotations.ThreadSafe
import site.syzk.dataflow.core.Feedback
import site.syzk.dataflow.core.Feedback.*
import site.syzk.dataflow.core.Link
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * 目的节点的通用内核
 * @param action 目的节点接收事件后的动作
 */
@ThreadSafe
internal class TargetCore<T>(
        private val maxParallelismDegree: Int,
        private val action: (T) -> Unit
) {
    private val parallelismDegree = AtomicInteger(0)
    private val waitingQueue = mutableListOf<Pair<Long, Link<T>>>()

    fun offer(eventId: Long, link: Link<T>): Feedback =
            if (parallelismDegree.incrementAndGet() > maxParallelismDegree) {
                synchronized(waitingQueue) { waitingQueue.add(eventId to link) }
                parallelismDegree.decrementAndGet()
                Postponed
            } else link.source.consume(eventId, link)
                    .let { pair ->
                        if (pair.first) {
                            @Suppress("UNCHECKED_CAST")
                            thread {
                                action(pair.second as T)
                                parallelismDegree.decrementAndGet()
                                synchronized(waitingQueue) {
                                    if (waitingQueue.isNotEmpty()) {
                                        val temp = waitingQueue.first()
                                        waitingQueue.removeAt(0)
                                        offer(temp.first, temp.second)
                                    }
                                }
                            }
                            Accepted
                        } else
                            NotAvailable
                    }
}
