package org.mechdancer.dataflow.core

import org.mechdancer.dataflow.blocks.ActionBlock
import org.mechdancer.dataflow.blocks.BroadcastBlock
import org.mechdancer.dataflow.blocks.BufferBlock
import org.mechdancer.dataflow.blocks.TransformBlock

//-------------------------------
// type
//-------------------------------

typealias IBridgeBlock<T> = IPropagatorBlock<T, T>

//-------------------------------
// post
//-------------------------------

infix fun <T> ITarget<T>.post(event: T) =
    defaultSource.offer(event).let { offer(it.first, it.second) }

//-------------------------------
// link
//-------------------------------

infix fun <T> ISource<T>.linkTo(target: ITarget<T>) =
    linkTo(target)

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
