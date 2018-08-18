package org.mechdancer.dataflow.external.eventbus

import org.mechdancer.dataflow.blocks.BroadcastBlock
import org.mechdancer.dataflow.core.Link
import org.mechdancer.dataflow.core.linkTo
import org.mechdancer.dataflow.core.post
import org.mechdancer.dataflow.external.eventbus.annotations.Subscribe
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.starProjectedType

class EventBusImpl internal constructor() : EventBus {

	private val stickyEvents: ConcurrentHashMap<KClass<*>, IEvent> = ConcurrentHashMap()

	private val broadcast = BroadcastBlock<IEvent>()
	private val links = hashMapOf<KFunction<Unit>, Link<IEvent>>()

	fun subscribe(receiver: Any, kFunction: KFunction<Unit>) {
		kFunction.let { f ->
			links[f] = broadcast linkTo {
				kFunction.call(receiver, it)
			}
		}
	}

	fun unSubscribe(kFunction: KFunction<Unit>) {
		links.remove(kFunction)?.dispose()
	}

	override fun register(receiver: Any) {
		receiver::class.memberFunctions.filter {
			it.returnType == Unit::class.starProjectedType
		}.forEach { f ->
			f.findAnnotation<Subscribe>()?.let {
				@Suppress("UNCHECKED_CAST")
				subscribe(receiver, f as KFunction<Unit>)
				if (it.sticky)
					stickyEvents.forEach { _, e ->
						f.call(receiver, e)
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
				unSubscribe(f as KFunction<Unit>)
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

	override fun getStickyEvent(kClass: KClass<*>): IEvent? = stickyEvents[kClass]

	override fun removeStickyEvent(kClass: KClass<*>): Boolean =
			stickyEvents.remove(kClass)?.run { true } ?: false


	override fun removeAllStickyEvents() {
		stickyEvents.clear()
	}

}