package org.mechdancer.dataflow.core.internal

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.mechdancer.dataflow.annotations.ThreadSafety
import org.mechdancer.dataflow.core.ExecutableOptions
import org.mechdancer.dataflow.core.Feedback
import org.mechdancer.dataflow.core.Feedback.*
import org.mechdancer.dataflow.core.IEgress
import org.mechdancer.dataflow.core.IIngress
import java.io.Closeable
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

/**
 * 目的节点的通用内核
 * @param action 目的节点接收事件后的动作
 */
@ThreadSafety
internal class TargetCore<T>(
        private val options: ExecutableOptions = ExecutableOptions(),
        private val action: suspend (T) -> Unit
) : IIngress<T>, CoroutineScope, Closeable {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = options.executor + CoroutineName("core") + job

    private val parallelismDegree = AtomicInteger(0)
    private val waitingQueue = ConcurrentLinkedQueue<Pair<Long, IEgress<T>>>()

    private fun bind(id: Long, link: IEgress<T>) = waitingQueue.add(id to link)
    private fun unbind(): Pair<Long, IEgress<T>>? = waitingQueue.poll()

    override fun close() = job.cancel()

    override suspend fun offer(id: Long, egress: IEgress<T>): Feedback =
            if (parallelismDegree.incrementAndGet() > options.parallelismDegree) {
                bind(id, egress)
                parallelismDegree.decrementAndGet()
                Postponed
            } else {
                val message = egress.consume(id)
                if (message.hasValue) {
                    runBlocking {
                        action(message.value)
                        parallelismDegree.decrementAndGet()
                        while (parallelismDegree.get() < options.parallelismDegree)
                            unbind()?.let { (id, egress) -> offer(id, egress) } ?: break
                    }
                    Accepted
                } else {
                    parallelismDegree.decrementAndGet()
                    NotAvailable
                }
            }
}
