package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.DefaultSource
import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.TargetCore
import java.util.concurrent.atomic.AtomicLong

/**
 * 广播节点
 * 堆中的事件只会被新事件顶替，不会因为接收而消耗
 */
class BroadcastBlock<T> : ITarget<T>, ISource<T> {
    override val defaultSource = DefaultSource<T>()

    /**
     * 存储已链接的节点
     */
    private val targets = mutableListOf<ITarget<T>>()

    /**
     * 唯一Id分配器
     */
    private val id = AtomicLong(0)

    /**
     * 堆
     */
    private val buffer = hashMapOf<Long, T>()

    /**
     * 作为目的节点的内核
     * 新到来的事件顶替旧事件，然后向所有目的节点通报事件到来
     */
    private val targetCore = TargetCore<T> {
        val newId = id.incrementAndGet()
        synchronized(buffer) {
            buffer.clear()
            buffer[newId] = it
        }
        synchronized(targets) {
            for (target in targets)
                target.offer(newId, this)
        }
    }

    /**
     * 源通报事件到来时调用
     */
    override fun offer(eventId: Long, source: ISource<T>) =
            targetCore.offer(eventId, source)

    /**
     * 目的节点获取事件时调用
     */
    override fun consume(id: Long) =
            buffer.containsKey(id) to buffer[id]

    /**
     * 链接新目的节点
     */
    override fun linkTo(target: ITarget<T>) {
        synchronized(target) { targets.add(target) }
    }
}
