package site.syzk.dataflow.core.internal

import site.syzk.dataflow.annotations.ThreadSafe
import site.syzk.dataflow.core.ExecutableOptions
import site.syzk.dataflow.core.Feedback
import site.syzk.dataflow.core.Feedback.*
import site.syzk.dataflow.core.Link
import site.syzk.dataflow.core.executableOptions
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicInteger

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
    private val waitingQueue = ConcurrentLinkedQueue<Pair<Long, Link<T>>>()
    private val executor =
            options.dispatcher
                    ?: ThreadPoolExecutor(
                            0,
                            options.parallelismDegree,
                            100,
                            MILLISECONDS,
                            LinkedBlockingQueue<Runnable>(4)
                    )

    private fun bind(id: Long, link: Link<T>) = waitingQueue.add(id to link)

    private fun unbind(): Pair<Long, Link<T>>? = waitingQueue.poll()

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
                                //options.dispatcher?.execute(task) ?: thread(block = task)
                                executor.execute(task)
                                Accepted
                            } else {
                                parallelismDegree.decrementAndGet()
                                NotAvailable
                            }
                        }
}
