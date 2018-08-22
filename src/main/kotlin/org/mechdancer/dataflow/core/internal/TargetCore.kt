package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.annotations.ThreadSafety
import org.mechdancer.dataflow.core.ExecutableOptions
import org.mechdancer.dataflow.core.Feedback
import org.mechdancer.dataflow.core.Feedback.*
import org.mechdancer.dataflow.core.Link
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicInteger

/**
 * 目的节点的通用内核
 * @param action 目的节点接收事件后的动作
 */
@ThreadSafety
internal class TargetCore<T>(
        private val options: ExecutableOptions = ExecutableOptions(),
        private val action: (T) -> Unit
) {
    private companion object {
        val defaultDispatcher = ForkJoinPool()
    }

    private val parallelismDegree = AtomicInteger(0)
    private val waitingQueue = ConcurrentLinkedQueue<Pair<Long, Link<T>>>()

    private fun bind(id: Long, link: Link<T>) = waitingQueue.add(id to link)
    private fun unbind(): Pair<Long, Link<T>>? = waitingQueue.poll()

    fun offer(id: Long, link: Link<T>): Feedback =
            if (parallelismDegree.incrementAndGet() > options.parallelismDegree) {
                bind(id, link)
                parallelismDegree.decrementAndGet()
                Postponed
            } else
                link.consume(id)
                        .let { pair ->
                            if (pair.first) {
                                val task = {
                                    @Suppress("UNCHECKED_CAST")
                                    action(pair.second as T)
                                    parallelismDegree.decrementAndGet()
                                    while (parallelismDegree.get() < options.parallelismDegree)
                                        unbind()?.let { offer(it.first, it.second) } ?: break
                                }
                                (options.executor ?: defaultDispatcher).execute(task)
                                Accepted
                            } else {
                                parallelismDegree.decrementAndGet()
                                NotAvailable
                            }
                        }
}
