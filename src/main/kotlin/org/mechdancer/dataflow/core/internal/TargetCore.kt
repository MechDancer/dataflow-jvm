package org.mechdancer.dataflow.core.internal

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.mechdancer.dataflow.core.Feedback
import org.mechdancer.dataflow.core.Feedback.*
import org.mechdancer.dataflow.core.intefaces.IEgress
import org.mechdancer.dataflow.core.intefaces.IIngress
import org.mechdancer.dataflow.core.options.ExecutionOptions
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * Common core for target blocks
 * 目的节点的通用内核
 *
 * @param options optional settings to customize the behavior of executable block
 *                用于定制可执行模块行为的可选项
 * @param action action to execute after receiving events
 *               目的节点接收事件后的动作
 */
internal class TargetCore<T>(
    private val options: ExecutionOptions = ExecutionOptions(),
    private val action: suspend (T) -> Unit
) : IIngress<T> {

    // 当前并行度
    // 即正在运行的 action 的数量
    private val parallelismDegree = AtomicInteger(0)

    // 当前等待队列容量
    // 并发集合的 size() 操作通常具有 O(n) 时间复杂度，另外存储以提高性能
    private val queueSize = AtomicInteger(0)

    // 等待队列
    private val waitingQueue = ConcurrentLinkedQueue<Pair<Long, IEgress<T>>>()

    override fun offer(id: Long, egress: IEgress<T>): Feedback {
        // 更新并行数
        if (!parallelismDegree.increaseIf { it < options.parallelismDegree })
            return when {
                queueSize.increaseIf { it < options.queueSize } -> {
                    // 若可以加入队列则加入队列，并通知消息处理被推迟
                    waitingQueue.add(id to egress)
                    Postponed
                }
                else                                            ->
                    // 若当前等待队列已满则拒绝这个消息
                    Declined
            }
        // 否则从源消费这个消息
        egress
            .consume(id)
            .then {
                // 启动协程以处理消息
                GlobalScope.launch(options.executor) {
                    action(it)
                    // 利用同一个协程尽量处理排队的消息，直到队列空
                    while (true)
                        waitingQueue.poll()
                            ?.also { queueSize.decrementAndGet() }
                            ?.let { (id, egress) -> egress.consume(id) }
                            ?.then { action(it) }
                        ?: break
                    // 关闭协程前减小并行度
                    parallelismDegree.decrementAndGet()
                }
                // 得到消息则通知消息被接收
                return Accepted
            }
        // 不会启动协程，减小并行度
        parallelismDegree.decrementAndGet()
        // 消息已被消费，未能取得
        return NotAvailable
    }
}
