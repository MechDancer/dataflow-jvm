package org.mechdancer.dataflow.core.intefaces

import org.mechdancer.dataflow.blocks.StandardBlock
import org.mechdancer.dataflow.blocks.TargetType.Broadcast
import org.mechdancer.dataflow.core.Feedback
import org.mechdancer.dataflow.core.options.ExecutionOptions
import org.mechdancer.dataflow.core.options.LinkOptions
import java.io.Closeable

/**
 * Link between blocks
 *
 * It is a event exit having [uuid], can be closed.
 */
interface ILink<T> : IWithUUID,
                     IEgress<T>,
                     Closeable {

    /**
     * Source block
     */
    val source: ISource<T>
    /**
     * Target block
     */
    val target: ITarget<T>
    /**
     * Linking options
     */
    val options: LinkOptions<T>

    val count: Int
    val rest: Int

    /**
     * Is closed
     */
    val closed: Boolean

    /**
     * Notify event coming
     *
     * Must notified by source. There is no need to specify the source or accept messages from other sources.
     *
     * 通知事件到来
     *
     * 必然由源来通知，不必再指定源，也不接受其他源的消息
     */
    infix fun offer(id: Long): Feedback

    companion object {
        /**
         * Global topology change event
         *
         * 拓扑改变事件
         */
        internal val changed = StandardBlock<ILink<*>, ILink<*>>(
            name = "link changed",
            bufferSize = 1024,
            targetType = Broadcast,
            options = ExecutionOptions(1),
            map = { it }
        )
    }
}
