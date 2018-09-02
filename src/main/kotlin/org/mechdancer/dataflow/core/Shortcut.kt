package org.mechdancer.dataflow.core

import org.mechdancer.dataflow.blocks.*
import java.util.concurrent.TimeUnit

//-------------------------------
// type
//-------------------------------

typealias IBridgeBlock<T> = IPropagatorBlock<T, T>

fun <T> message(value: T) = Message(true, value)
fun message() = Message(false, null)

//-------------------------------
// post
//-------------------------------

infix fun <T> IPostable<T>.post(event: T) = defaultSource(event)

//-------------------------------
// link
//-------------------------------

infix fun <T> ISource<T>.linkTo(target: ITarget<T>) =
	linkTo(target, LinkOptions())

infix fun <T> ISource<T>.linkTo(target: (T) -> Unit) =
	linkTo(org.mechdancer.dataflow.blocks.ActionBlock(action = target))

fun <T> link(source: ISource<T>, target: ITarget<T>) =
	source.linkTo(target)

fun <T> link(source: ISource<T>, target: ITarget<T>, eventLimit: Int) =
	source.linkTo(target, LinkOptions(eventLimit = eventLimit))

fun <T> link(source: ISource<T>, target: ITarget<T>, predicate: (T) -> Boolean) =
	source.linkTo(target, LinkOptions(predicate))

fun <T> link(source: ISource<T>, target: ITarget<T>, options: LinkOptions<T>) =
	source.linkTo(target, options)

operator fun <T> ISource<T>.minus(target: ITarget<T>) =
	linkTo(target)

operator fun <TIn, TOut> ISource<TIn>.minus(target: (TIn) -> TOut) =
	TransformBlock(map = target).also { linkTo(it) }

//-------------------------------
// build
//-------------------------------

fun <T> action(
	name: String = "action",
	options: ExecutableOptions = ExecutableOptions(),
	action: (T) -> Unit
) = ActionBlock(name, options, action)

fun <T> broadcast(name: String = "broadcast") = BroadcastBlock<T>(name)

fun <T> buffer(name: String = "buffer") = BufferBlock<T>(name)

fun <TIn, TOut> transform(
	name: String = "transform",
	options: ExecutableOptions = ExecutableOptions(),
	map: (TIn) -> TOut
) = TransformBlock(name, options, map)

fun <T> delay(
	delay: Long,
	unit: TimeUnit = TimeUnit.MILLISECONDS,
	name: String = "delay"
) = DelayBlock<T>(name, delay, unit)

fun interval(
	period: Long,
	unit: TimeUnit = TimeUnit.MILLISECONDS,
	immediately: Boolean = true,
	name: String = "interval"
) = IntervalBlock(name, period, unit, immediately)
