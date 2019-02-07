package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.core.LinkOptions
import org.mechdancer.dataflow.core.UUIDBase
import org.mechdancer.dataflow.core.intefaces.ILink
import org.mechdancer.dataflow.core.intefaces.ILink.Companion.changed
import org.mechdancer.dataflow.core.intefaces.ILink.Companion.list
import org.mechdancer.dataflow.core.intefaces.ISource
import org.mechdancer.dataflow.core.intefaces.ITarget
import org.mechdancer.dataflow.core.intefaces.IWithUUID
import org.mechdancer.dataflow.core.post
import java.util.concurrent.atomic.AtomicInteger

/**
 * 链接信息
 *
 * @param source 事件源
 * @param target 事件宿
 * @param options 链接选项
 */
internal class Link<T>(
        override val source: ISource<T>,
        override val target: ITarget<T>,
        override val options: LinkOptions<T>,
        private val holder: LinkManager<T>
) : ILink<T>, IWithUUID by UUIDBase() {

    //构造时加入列表
    init {
        ILink.list.add(this)
        changed.post(list.toList())
    }

    private val _count = AtomicInteger(0)
    override val count get() = _count.get()
    override val rest get() = options.eventLimit - _count.get()

    override infix fun offer(id: Long) = target.offer(id, this)
    override infix fun consume(id: Long) =
            source.consume(id).apply {
                if (this.hasValue && _count.incrementAndGet() > options.eventLimit)
                    dispose()
            }

    override fun dispose() {
        holder.remove(this)
        list.remove(this).also { if (it) changed.post(list.toList()) }
    }

    override fun toString() = "$source -> $target"
}
