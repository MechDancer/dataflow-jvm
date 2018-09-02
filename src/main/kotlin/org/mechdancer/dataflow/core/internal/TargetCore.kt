package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.annotations.ThreadSafety
import org.mechdancer.dataflow.core.ExecutableOptions
import org.mechdancer.dataflow.core.Feedback
import org.mechdancer.dataflow.core.Feedback.*
import org.mechdancer.dataflow.core.IEgress
import org.mechdancer.dataflow.core.IIngress
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * 目的节点的通用内核
 * @param action 目的节点接收事件后的动作
 */
@ThreadSafety
internal class TargetCore<T>(
	private val options: ExecutableOptions = ExecutableOptions(),
	private val action: (T) -> Unit
) : IIngress<T> {
	private companion object;

	private val parallelismDegree = AtomicInteger(0)
	private val waitingQueue = ConcurrentLinkedQueue<Pair<Long, IEgress<T>>>()

	private fun bind(id: Long, link: IEgress<T>) = waitingQueue.add(id to link)
	private fun unbind(): Pair<Long, IEgress<T>>? = waitingQueue.poll()

	override fun offer(id: Long, egress: IEgress<T>): Feedback =
		if (parallelismDegree.incrementAndGet() > options.parallelismDegree) {
			bind(id, egress)
			parallelismDegree.decrementAndGet()
			Postponed
		} else
			egress.consume(id)
				.let { pair ->
					if (pair.hasValue) {
						val task = {
							action(pair.value)
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
