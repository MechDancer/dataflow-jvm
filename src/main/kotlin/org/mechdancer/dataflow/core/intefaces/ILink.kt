package org.mechdancer.dataflow.core.intefaces

import org.mechdancer.dataflow.blocks.StandardBlock
import org.mechdancer.dataflow.blocks.TargetType.Broadcast
import org.mechdancer.dataflow.core.Feedback
import org.mechdancer.dataflow.core.options.ExecutableOptions
import org.mechdancer.dataflow.core.options.LinkOptions
import java.io.Closeable

/**
 * 链接的定义
 */
interface ILink<T> : IWithUUID,
                     IEgress<T>,
                     Closeable {
    val source: ISource<T>
    val target: ITarget<T>
    val options: LinkOptions<T>

    val count: Int
    val rest: Int

    val closed: Boolean

    /**
     * 通知事件到来
     *
     * 必然由源来通知，不必再指定源，也不接受其他源的消息
     */
    infix fun offer(id: Long): Feedback

    companion object {
        /** 拓扑改变事件 */
        internal val changed = StandardBlock<ILink<*>, ILink<*>>(
            name = "link changed",
            bufferSize = 1024,
            targetType = Broadcast,
            options = ExecutableOptions(1),
            map = { it }
        )
    }
}
