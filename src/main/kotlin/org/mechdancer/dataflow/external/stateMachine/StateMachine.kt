package org.mechdancer.dataflow.external.stateMachine

import org.mechdancer.dataflow.core.DefaultSource
import org.mechdancer.dataflow.core.IBridgeBlock
import org.mechdancer.dataflow.core.IReceivable
import org.mechdancer.dataflow.core.Link
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.TargetCore
import org.mechdancer.dataflow.core.internal.otherwise
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 状态机
 * 本质是一系列状态的管理器
 * 广播方式收发状态转移记录
 */
class StateMachine<T>(override val name: String) :
    IBridgeBlock<MachineState<T>>,
    IReceivable<MachineState<T>> {
    override val uuid = UUID.randomUUID()!!
    override val defaultSource by lazy { DefaultSource(this) }

    internal val dispatcher = ThreadPoolExecutor(
        0, 1, 0, TimeUnit.MILLISECONDS,
        LinkedBlockingQueue<Runnable>())

    private val runningFlag = AtomicBoolean(false)
    val running get() = runningFlag.get()
    val ending = State(this) { it }

    private val receiveLock = Object()
    private val sourceCore = SourceCore<MachineState<T>>(1)
    private val targetCore = TargetCore<MachineState<T>> { s ->
        runningFlag.set(!(s.current === ending))
        val newId = sourceCore.offer(s)
        Link[this]
            .filter { it.options.predicate(s) }
            .any { it.target.offer(newId, it).positive }
            .otherwise { synchronized(receiveLock) { receiveLock.notifyAll() } }
    }

    override fun offer(id: Long, link: Link<MachineState<T>>) =
        targetCore.offer(id, link)

    override fun consume(id: Long): Pair<Boolean, MachineState<T>?> =
        sourceCore.get(id)

    override fun receive(): MachineState<T> {
        while (true) {
            synchronized(receiveLock) {
                sourceCore.get().let {
                    if (it.first)
                        @Suppress("UNCHECKED_CAST")
                        return it.second as MachineState<T>
                    else
                        receiveLock.wait()
                }
            }
        }
    }
}
