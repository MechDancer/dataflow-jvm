package site.syzk.dataflow.core

import site.syzk.dataflow.blocks.ActionBlock
import site.syzk.dataflow.blocks.BroadcastBlock
import site.syzk.dataflow.blocks.BufferBlock
import site.syzk.dataflow.blocks.TransformBlock

infix fun <T> ITarget<T>.post(event: T) =
		defaultSource.offer(event).let { offer(it.first, it.second) }

//-------------------------------
// link
//-------------------------------

infix fun <T> ISource<T>.linkTo(target: ITarget<T>) =
		linkTo(target)

infix fun <T> ISource<T>.linkTo(target: (T) -> Unit) =
		linkTo(ActionBlock(action = target))

fun <T> link(source: ISource<T>, target: ITarget<T>) =
		source.linkTo(target)

fun <T> link(source: ISource<T>, target: ITarget<T>, count: Int) =
		source.linkTo(target, linkOptions(count))

fun <T> link(source: ISource<T>, target: ITarget<T>, predicate: (T) -> Boolean) =
		source.linkTo(target, linkOptions(predicate))

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
		options: ExecutableOptions = executableOptions(),
		action: (T) -> Unit
) = ActionBlock(name, options, action)

fun <T> broadcast(name: String = "broadcast") = BroadcastBlock<T>(name)

fun <T> buffer(name: String = "buffer") = BufferBlock<T>(name)

fun <TIn, TOut> transform(
		name: String = "transform",
		options: ExecutableOptions = executableOptions(),
		map: (TIn) -> TOut
) = TransformBlock(name, options, map)