package org.mechdancer.dataflow.external.eventbus

import org.mechdancer.dataflow.blocks.BroadcastBlock
import org.mechdancer.dataflow.core.Link
import org.mechdancer.dataflow.core.linkTo
import org.mechdancer.dataflow.core.post
import org.mechdancer.dataflow.external.eventbus.annotations.Subscribe
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.starProjectedType

class EventBusImpl : EventBus {

	private val stickyEvents: ConcurrentHashMap<KClass<*>, IEvent> = ConcurrentHashMap()

	private val broadcast = BroadcastBlock<IEvent>()
	private val links = hashMapOf<KFunction<Unit>, Pair<Link<IEvent>, Executor?>>()

	fun subscribe(receiver: Any, kFunction: KFunction<Unit>) {
		kFunction.let { f ->
			links[f] = broadcast linkTo {
				if (it::class.starProjectedType ==
						f.parameters[1].type ||
						IEvent::class.starProjectedType ==
						f.parameters[1].type)
					links[f]?.second?.execute {
						f.call(receiver, it)
					} ?: f.call(receiver, it)
			} to null
		}
	}

	fun unsubscribe(kFunction: KFunction<Unit>) {
		links.remove(kFunction)?.first?.dispose()
	}

	fun setDispatcher(kFunction: KFunction<Unit>, dispatcher: Executor?) {
		links[kFunction]?.let {
			links[kFunction] = it.copy(second = dispatcher)
		}
	}

	override fun register(receiver: Any) {
		receiver::class.memberFunctions.filter {
			it.returnType == Unit::class.starProjectedType
		}.forEach { f ->
			f.findAnnotation<Subscribe>()?.let { subscribe ->
				@Suppress("UNCHECKED_CAST")
				subscribe(receiver, f as KFunction<Unit>)
				if (subscribe.sticky)
					stickyEvents.forEach { _, e ->
						links[f]?.first?.target?.post(e)
					}
			}
		}
	}

	override fun unregister(receiver: Any) {
		receiver::class.memberFunctions.filter {
			it.returnType == Unit::class.starProjectedType
		}.forEach { f ->
			f.annotations.find { it is Subscribe }?.let {
				@Suppress("UNCHECKED_CAST")
				unsubscribe(f as KFunction<Unit>)
			}
		}
	}

	override fun post(event: IEvent) {
		broadcast post event
	}

	override fun postSticky(event: IEvent) {
		stickyEvents[event::class] = event
		post(event)
	}

	override fun getStickyEvent(kClass: KClass<out IEvent>): IEvent? = stickyEvents[kClass]

	override fun removeStickyEvent(kClass: KClass<out IEvent>): Boolean =
			stickyEvents.remove(kClass)?.run { true } ?: false


	override fun removeAllStickyEvents() {
		stickyEvents.clear()
	}

}