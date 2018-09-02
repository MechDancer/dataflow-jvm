package org.mechdancer.dataflow.external.stateMachine.core

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.IPostable.DefaultSource
import org.mechdancer.dataflow.core.internal.LinkManager
import org.mechdancer.dataflow.core.internal.ReceiveCore
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.TargetCore
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
	IBridgeBlock<MachineSnapshot<T>>,
	IReceivable<MachineSnapshot<T>>,
	IPostable<MachineSnapshot<T>> {
	internal val dispatcher = ThreadPoolExecutor(
		0, 1, 0, TimeUnit.MILLISECONDS,
		LinkedBlockingQueue<Runnable>())

	private val runningFlag = AtomicBoolean(false)
	val running get() = runningFlag.get()
	val ending = StateMember(this) { it }

	private val linkManager = LinkManager(this)
	private val receiveCore = ReceiveCore()
	private val sourceCore = SourceCore<MachineSnapshot<T>>(1)
	private val targetCore = TargetCore<MachineSnapshot<T>> { s ->
		runningFlag.set(!(s.current === ending))
		sourceCore.offer(s).let { newId ->
			linkManager[newId, s]
			receiveCore.call()
		}
		receiveCore.call()
	}

	override val uuid = UUID.randomUUID()!!
	override val defaultSource by lazy { DefaultSource(this) }
	override val targets get() = linkManager.targets

	override fun offer(id: Long, egress: IEgress<MachineSnapshot<T>>) = targetCore.offer(id, egress)
	override fun consume(id: Long): Message<out MachineSnapshot<T>> = sourceCore[id]
	override fun receive() = receiveCore getFrom sourceCore
	override fun linkTo(target: ITarget<MachineSnapshot<T>>,
	                    options: LinkOptions<MachineSnapshot<T>>)
		: ILink<MachineSnapshot<T>> =
		linkManager.linkTo(target, options)
}
