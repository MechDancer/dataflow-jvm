package site.syzk.dataflow.core.internal

import site.syzk.dataflow.annotations.ThreadSafe
import site.syzk.dataflow.core.ExecutableOptions
import site.syzk.dataflow.core.Feedback
import site.syzk.dataflow.core.Feedback.*
import site.syzk.dataflow.core.Link
import site.syzk.dataflow.core.executableOptions
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * 目的节点的通用内核
 * @param action 目的节点接收事件后的动作
 */
@ThreadSafe
internal class TargetCore<T>(
        private val options: ExecutableOptions = executableOptions(),
        private val action: (T) -> Unit
) {
    private val parallelismDegree = AtomicInteger(0)
    private val waitingQueue = mutableListOf<Pair<Long, Link<T>>>()

    private fun bind(id: Long, link: Link<T>) {
        synchronized(waitingQueue) { waitingQueue.add(id to link) }
    }

    private fun unbind(): Pair<Long, Link<T>>? {
        synchronized(waitingQueue) {
            return if (waitingQueue.isNotEmpty()) {
                val pair = waitingQueue.first()
                waitingQueue.removeAt(0)
                pair
            } else null
        }
    }

    fun offer(id: Long, link: Link<T>): Feedback =
            if (parallelismDegree.incrementAndGet() > options.parallelismDegree) {
                bind(id, link)
                parallelismDegree.decrementAndGet()
                Postponed
            } else
                link.source.consume(id, link)
                        .let { pair ->
                            if (pair.first) {
                                val task = {
                                    @Suppress("UNCHECKED_CAST")
                                    action(pair.second as T)
                                    parallelismDegree.decrementAndGet()
                                    while (true)
                                        unbind()?.let { offer(it.first, it.second) } ?: break
                                }
                                options.dispatcher?.execute(task) ?: thread(block = task)
                                Accepted
                            } else {
                                parallelismDegree.decrementAndGet()
                                NotAvailable
                            }
                        }
}
