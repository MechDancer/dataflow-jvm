package org.mechdancer.dataflow.core.internal

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.mechdancer.dataflow.annotations.ThreadSafety
import org.mechdancer.dataflow.core.ExecutableOptions
import org.mechdancer.dataflow.core.Feedback
import org.mechdancer.dataflow.core.Feedback.*
import org.mechdancer.dataflow.core.Message
import org.mechdancer.dataflow.core.intefaces.IEgress
import org.mechdancer.dataflow.core.intefaces.IIngress
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * 目的节点的通用内核
 *
 * @param action 目的节点接收事件后的动作
 */
@ThreadSafety
internal class TargetCore<T>(
    private val options: ExecutableOptions = ExecutableOptions(),
    private val action: suspend (T) -> Unit
) : IIngress<T> {

    private val parallelismDegree = AtomicInteger(0)
    private val waitingQueue = ConcurrentLinkedQueue<Pair<Long, IEgress<T>>>()

    override fun offer(id: Long, egress: IEgress<T>): Feedback {
        var feedback = NotAvailable
        var msg: Message<out T>? = null

        parallelismDegree.updateAndGet {
            if (it >= options.parallelismDegree) {
                feedback = Postponed
                it
            } else {
                msg = egress.consume(id)
                if (msg!!.hasValue) {
                    feedback = Accepted
                    it + 1
                } else {
                    feedback = NotAvailable
                    it
                }
            }
        }

        when (feedback) {
            Accepted     -> GlobalScope.launch(options.executor) {
                action(msg!!.value)
                while (true)
                    (waitingQueue.poll() ?: break)
                        .let { (id, egress) -> egress.consume(id) }
                        .takeIf { it.hasValue }
                        ?.let { action(it.value) }
                parallelismDegree.decrementAndGet()
            }
            Postponed    -> waitingQueue.add(id to egress)
            NotAvailable -> Unit
            Declined     -> Unit
        }

        return feedback
    }
}
