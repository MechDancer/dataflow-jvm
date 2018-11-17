package org.mechdancer.dataflow.external.statemachine.core

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.IPostable.DefaultSource
import org.mechdancer.dataflow.core.internal.LinkManager
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.TargetCore
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
    : IBridgeBlock<T>, IPostable<T> {
    private val linkManager = LinkManager(this)
    private val loopLink = if (loop) linkManager.linkTo(this, LinkOptions()) else null
    private val sourceCore = SourceCore<T>(Int.MAX_VALUE)
    private val targetCore = TargetCore<T>(
            ExecutableOptions(executor = owner.dispatcher)
    ) { event ->
        val out = action(event)
        owner post MachineSnapshot(this, out)
        sourceCore.offer(out).let {
            //			ILink[this]
//				.filter { it.options.predicate(out) }
//				.dropWhile { it === loopLink }
//				.all { it.target.offer(newId, it).negative }
//				.then { loopLink?.offer(newId) }
        }
    }

    override val uuid = UUID.randomUUID()!!
    override val defaultSource by lazy { DefaultSource(this) }
    override val targets get() = linkManager.targets

    override fun offer(id: Long, egress: IEgress<T>) = targetCore.offer(id, egress)
    override fun consume(id: Long) = sourceCore.consume(id)
    override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) =
            linkManager.linkTo(target, options)
}
