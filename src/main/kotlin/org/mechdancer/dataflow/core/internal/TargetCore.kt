package org.mechdancer.dataflow.core.internal

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.mechdancer.dataflow.annotations.ThreadSafety
import org.mechdancer.dataflow.core.Feedback
import org.mechdancer.dataflow.core.Feedback.*
import org.mechdancer.dataflow.core.intefaces.IEgress
import org.mechdancer.dataflow.core.intefaces.IIngress
import org.mechdancer.dataflow.core.options.ExecutionOptions
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * Common kernel for target block
 *
 * 目的节点的通用内核
 *
 * @param action action after receiving events 目的节点接收事件后的动作
 */
@ThreadSafety
internal class TargetCore<T>(
        private val options: ExecutionOptions = ExecutionOptions(),
        private val action: suspend (T) -> Unit
) : IIngress<T> {

    private val parallelismDegree = AtomicInteger(0)
    private val queueSize = AtomicInteger(0)
    private val waitingQueue = ConcurrentLinkedQueue<Pair<Long, IEgress<T>>>()

    override fun offer(id: Long, egress: IEgress<T>): Feedback {
        var feedback = Declined

        if (parallelismDegree.incrementAndGet() > options.parallelismDegree) {
            parallelismDegree.decrementAndGet()
            feedback =
                if (queueSize.incrementAndGet() > options.queueSize) {
                    queueSize.decrementAndGet()
                    Declined
                } else {
                    waitingQueue.add(id to egress)
                    Postponed
                }
        } else
            egress
                .consume(id)
                .then {
                    feedback = Accepted
                    GlobalScope.launch(options.executor) {
                        action(it)
                        while (true)
                            waitingQueue.poll()
                                ?.also { queueSize.decrementAndGet() }
                                ?.let { (id, egress) -> egress.consume(id) }
                                ?.then { action(it) }
                            ?: break
                        parallelismDegree.decrementAndGet()
                    }
                }
                .otherwise {
                    feedback = NotAvailable
                    parallelismDegree.decrementAndGet()
                }

        return feedback
    }
}
