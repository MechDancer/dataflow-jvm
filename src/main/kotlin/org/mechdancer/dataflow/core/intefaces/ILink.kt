package org.mechdancer.dataflow.core.intefaces

import org.mechdancer.dataflow.core.Feedback
import org.mechdancer.dataflow.core.LinkOptions
import org.mechdancer.dataflow.core.buffer
import java.io.Closeable
import java.util.concurrent.ConcurrentSkipListSet

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
        /** 全局链接列表 */
        val list = ConcurrentSkipListSet<ILink<*>>()

        /** 拓扑改变事件 */
        val changed =
            buffer<List<ILink<*>>>("LinkInfo", 20)
    }
}
