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

    private fun bound(id: Long, link: Link<T>) {
        synchronized(waitingQueue) { waitingQueue.add(id to link) }
    }

    private fun unbound(): Pair<Long, Link<T>>? {
        synchronized(waitingQueue) {
            return if (waitingQueue.isNotEmpty()) {
                val pair = waitingQueue.first()
                waitingQueue.removeAt(0)
                pair
            } else null
        }
    }

    fun offer(eventId: Long, link: Link<T>): Feedback =
            if (parallelismDegree.incrementAndGet() > maxParallelismDegree) {
                bound(eventId, link)
                parallelismDegree.decrementAndGet()
                Postponed
            } else link.source.consume(eventId, link)
                    .let { pair ->
                        if (pair.first) {
                            @Suppress("UNCHECKED_CAST")
                            thread(name = "target") {
                                action(pair.second as T)
                                parallelismDegree.decrementAndGet()
                                while (true) {
                                    unbound()
                                            ?.let { offer(it.first, it.second) }
                                            ?: break
                                }
                            }
                            Accepted
                        } else
                            NotAvailable
                    }
}
