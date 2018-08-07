package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.DefaultSource
import site.syzk.dataflow.core.IReceivable
import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.internal.LinkManager
import site.syzk.dataflow.core.internal.TargetCore
import java.util.concurrent.atomic.AtomicLong

/**
 * 广播节点
 * 堆中的事件只会被新事件顶替，不会因为接收而消耗
 */
class BroadcastBlock<T> : ITarget<T>, ISource<T>, IReceivable<T> {
    override val defaultSource = DefaultSource<T>()

    /**
     * 存储已链接的节点
     */
    private val manager = LinkManager(this)

    /**
     * 唯一Id分配器
     */
    private val id = AtomicLong(0)

    /**
     * 堆
     */
    private val buffer = hashMapOf<Long, T>()

    //--------------------------
    // IReceivable
    //--------------------------
    private val receiveLock = Object()
    private var receivable = false
    private var value: T? = null

    /**
     * 作为目的节点的内核
     * 新到来的事件顶替旧事件，然后向所有目的节点通报事件到来
     */
    private val targetCore = TargetCore<T> { event ->
        val newId = id.incrementAndGet()
        synchronized(buffer) {
            buffer.clear()
            buffer[newId] = event
        }
        manager.targets
                .forEach { it.offer(newId, this) }
        synchronized(receiveLock) {
            receivable = true
            value = event
            receiveLock.notifyAll()
        }
    }

    /**
     * 源通报事件到来时调用
     */
    override fun offer(id: Long, source: ISource<T>) =
            targetCore.offer(id, source)

    /**
     * 目的节点获取事件时调用
     */
    override fun consume(id: Long) =
            buffer.containsKey(id) to buffer[id]

    override fun linkTo(target: ITarget<T>) = manager.linkTo(target)
    override fun unlink(target: ITarget<T>) = manager.unlink(target)

    override fun receive(): T {
        synchronized(receiveLock) {
            while (!receivable) receiveLock.wait()
            @Suppress("UNCHECKED_CAST") return value as T
        }
    }
}
