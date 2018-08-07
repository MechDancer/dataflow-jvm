package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.*
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
    private val targets = mutableListOf<ITarget<T>>()

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
        synchronized(receiveLock) {
            receivable = true
            value = it
            receiveLock.notifyAll()
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
    override fun linkTo(target: ITarget<T>): Link<T> {
        synchronized(target) { targets.add(target) }
        return Link(this, target)
    }

    override fun unlink(target: ITarget<T>) {
        synchronized(target) { targets.remove(target) }
    }

    override fun receive(): T {
        synchronized(receiveLock) {
            if (!receivable) receiveLock.wait()
            @Suppress("UNCHECKED_CAST")
            return value as T
        }
    }
}
