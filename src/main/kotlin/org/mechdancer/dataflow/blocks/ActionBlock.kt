package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.BlockBase
import org.mechdancer.dataflow.core.intefaces.IBlock
import org.mechdancer.dataflow.core.intefaces.IEgress
import org.mechdancer.dataflow.core.intefaces.IEntranceBlock
import org.mechdancer.dataflow.core.intefaces.IPostable.DefaultSource
import org.mechdancer.dataflow.core.internal.TargetCore
import org.mechdancer.dataflow.core.options.ExecutionOptions

/**
 * Action block
 *
 * Performs corresponding *action* on each message.
 *
 * 动作块
 *
 * 对每个到来的消息执行对应的动作
 */
class ActionBlock<T>(
        name: String = "action",
        options: ExecutionOptions = ExecutionOptions(),
        action: suspend (T) -> Unit
) : IEntranceBlock<T>, IBlock by BlockBase(name) {

    override val defaultSource by lazy { DefaultSource(this) }

    private val core = TargetCore(options, action)

    override fun offer(id: Long, egress: IEgress<T>) = core.offer(id, egress)

}
