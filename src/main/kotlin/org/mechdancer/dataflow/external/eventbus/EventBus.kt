package org.mechdancer.dataflow.external.eventbus

import kotlin.reflect.KClass

interface EventBus {

	fun register(receiver: Any)
	fun unregister(receiver: Any)

	fun post(event: IEvent)
	fun postSticky(event: IEvent)

	fun getStickyEvent(kClass: KClass<*>):IEvent?
	fun removeStickyEvent(kClass: KClass<*>): Boolean
	fun removeAllStickyEvents()
	companion object {
		val getDefault: EventBus by lazy { EventBusImpl() }
	}
}