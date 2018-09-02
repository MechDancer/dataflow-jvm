package org.mechdancer.dataflow.external.stateMachine.core

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.internal.Link
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.TargetCore
import org.mechdancer.dataflow.core.internal.then
import java.util.*

/**
 * 状态
 * 连接到状态机的状态节点
 */
class StateMember<T>(
    private val owner: StateMachine<T>,
    loop: Boolean = false,
    override val name: String = "StateMember",
    private val action: (T) -> T)
    : IBridgeBlock<T> {
    override val uuid = UUID.randomUUID()!!
    override val defaultSource by lazy { DefaultSource(this) }

    private val loopLink = if (loop) Link(this, this, LinkOptions()) else null

    private val sourceCore = SourceCore<T>(Int.MAX_VALUE)
    private val targetCore = TargetCore<T>(
        ExecutableOptions(executor = owner.dispatcher)
    ) { event ->
        val out = action(event)
        owner post MachineSnapshot(this, out)
        sourceCore.offer(out).let { newId ->
            Link[this]
                .filter { it.options.predicate(out) }
                .dropWhile { it === loopLink }
                .all { it.target.offer(newId, it).negative }
                .then { loopLink?.offer(newId) }
        }
    }

    override fun offer(id: Long, egress: IEgress<T>) = targetCore.offer(id, egress)
    override fun consume(id: Long) = sourceCore.consume(id)
}
