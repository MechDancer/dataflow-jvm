package org.mechdancer.dataflow.core

import org.mechdancer.dataflow.blocks.*
import java.util.concurrent.TimeUnit

//-------------------------------
// type
//-------------------------------

/** 桥接模块 := 不改变事件类型的传递模块 */
typealias IBridgeBlock<T> = IPropagatorBlock<T, T>

/** 构造事件信息 */
fun <T> message(value: T) = Message(true, value)

/** 构造空信息 */
fun message() = Message(false, null)

//-------------------------------
// post
//-------------------------------

/** 直接发送事件 */
infix fun <T> IPostable<T>.post(event: T) = defaultSource(event)

//-------------------------------
// link
//-------------------------------

/** 中缀链接 */
infix fun <T> ISource<T>.linkTo(target: ITarget<T>) =
	linkTo(target, LinkOptions())

/** 中缀链接 */
infix fun <T> ISource<T>.linkTo(target: (T) -> Unit) =
	linkTo(org.mechdancer.dataflow.blocks.ActionBlock(action = target))

/** 构造链接 */
fun <T> link(source: ISource<T>, target: ITarget<T>) =
	source.linkTo(target)

/** 构造链接 */
fun <T> link(source: ISource<T>, target: ITarget<T>, eventLimit: Int) =
	source.linkTo(target, LinkOptions(eventLimit = eventLimit))

/** 构造链接 */
fun <T> link(source: ISource<T>, target: ITarget<T>, predicate: (T) -> Boolean) =
	source.linkTo(target, LinkOptions(predicate))

/** 构造链接 */
fun <T> link(source: ISource<T>, target: ITarget<T>, options: LinkOptions<T>) =
	source.linkTo(target, options)

/** 构造链接 */
operator fun <T> ISource<T>.minus(target: ITarget<T>) =
	linkTo(target)

/** 构造链接 */
operator fun <TIn, TOut> ISource<TIn>.minus(target: (TIn) -> TOut) =
	TransformBlock(map = target).also { linkTo(it) }

//-------------------------------
// build
//-------------------------------

/** 构造节点 */
fun <T> action(
	name: String = "action",
	options: ExecutableOptions = ExecutableOptions(),
	action: (T) -> Unit
) = ActionBlock(name, options, action)

/** 构造节点 */
fun <T> broadcast(name: String = "broadcast") = BroadcastBlock<T>(name)

/** 构造节点 */
fun <T> buffer(name: String = "buffer") = BufferBlock<T>(name)

/** 构造节点 */
fun <TIn, TOut> transform(
	name: String = "transform",
	options: ExecutableOptions = ExecutableOptions(),
	map: (TIn) -> TOut
) = TransformBlock(name, options, map)

/** 构造节点 */
fun <T> delay(
	delay: Long,
	unit: TimeUnit = TimeUnit.MILLISECONDS,
	name: String = "delay"
) = DelayBlock<T>(name, delay, unit)

/** 构造节点 */
fun interval(
	period: Long,
	unit: TimeUnit = TimeUnit.MILLISECONDS,
	immediately: Boolean = true,
	name: String = "interval"
) = IntervalBlock(name, period, unit, immediately)
