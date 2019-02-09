package org.mechdancer.dataflow.external.eventbus

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import org.mechdancer.dataflow.blocks.BroadcastBlock
import org.mechdancer.dataflow.core.action
import org.mechdancer.dataflow.core.intefaces.ILink
import org.mechdancer.dataflow.core.intefaces.IPostable
import org.mechdancer.dataflow.core.linkTo
import org.mechdancer.dataflow.core.options.ExecutableOptions
import org.mechdancer.dataflow.core.post
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.starProjectedType

@Suppress("UNCHECKED_CAST")
class EventBusImpl : EventBus {
    private val stickyEvents = ConcurrentHashMap<KClass<*>, Event>()

    private val broadcast = BroadcastBlock<Event>()
    private val links = hashMapOf<KFunction<Unit>, ILink<Event>>()

    private fun subscribe(receiver: Any, kFunction: KFunction<Unit>, executor: Executor?) {
        kFunction.let { f ->
            links[f] = broadcast linkTo action(
                options = ExecutableOptions(executor = executor?.asCoroutineDispatcher()
                                                       ?: Dispatchers.Default)
            ) {
                if (it::class.starProjectedType == f.parameters[1].type ||
                    Event::class.starProjectedType == f.parameters[1].type
                ) f.call(receiver, it)
            }
        }
    }

    private fun unsubscribe(kFunction: KFunction<Unit>) {
        links.remove(kFunction)?.close()
    }

    override fun register(receiver: Any) {
        receiver::class.memberFunctions.filter {
            it.returnType == Unit::class.starProjectedType
        }.forEach { f ->
            f.findAnnotation<Subscribe>()?.let { subscribe ->
                @Suppress("UNCHECKED_CAST")
                subscribe(
                    receiver,
                    f as KFunction<Unit>,
                    EventBus.executors[subscribe.executor])
                if (subscribe.sticky)
                    stickyEvents.forEach { _, e ->
                        (links[f]?.target as? IPostable<Event>)?.post(e)
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

    override fun post(event: Event) {
        broadcast post event
    }

    override fun postSticky(event: Event) {
        stickyEvents[event::class] = event
        post(event)
    }

    override fun getStickyEvent(kClass: KClass<out Event>): Event? = stickyEvents[kClass]

    override fun removeStickyEvent(kClass: KClass<out Event>): Boolean =
        stickyEvents.remove(kClass)?.run { true } ?: false

    override fun removeAllStickyEvents() {
        stickyEvents.clear()
    }
}
