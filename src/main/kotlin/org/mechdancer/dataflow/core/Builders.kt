package org.mechdancer.dataflow.core

import kotlinx.coroutines.delay
import org.mechdancer.dataflow.blocks.ActionBlock
import org.mechdancer.dataflow.blocks.StandardBlock
import org.mechdancer.dataflow.blocks.TargetType.Broadcast
import org.mechdancer.dataflow.blocks.TargetType.Normal
import org.mechdancer.dataflow.core.intefaces.IPostable
import org.mechdancer.dataflow.core.intefaces.ISource
import org.mechdancer.dataflow.core.intefaces.ITarget
import org.mechdancer.dataflow.core.options.ExecutionOptions
import org.mechdancer.dataflow.core.options.LinkOptions

//-------------------------------
// post
//-------------------------------

/**
 * Posts [event] directly
 *
 * 直接发送事件
 */
infix fun <T> IPostable<T>.post(event: T) = defaultSource(event)

//-------------------------------
// link
//-------------------------------

/**
 * Infix link, see [linkTo]
 *
 * 中缀链接
 */
infix fun <T> ISource<T>.linkTo(target: ITarget<T>) =
        linkTo(target, LinkOptions())

/**
 * Infix link, see [linkTo]
 *
 * 中缀链接
 */
infix fun <T> ISource<T>.linkTo(target: suspend (T) -> Unit) =
        linkTo(ActionBlock(action = target))

/**
 * Builds a link between [source] and [target]
 *
 * 构造链接
 */
fun <T> link(source: ISource<T>, target: ITarget<T>) =
        source.linkTo(target)

/**
 * Builds a link between [source] and [target] specifying [eventLimit]
 *
 * 构造链接
 */
fun <T> link(source: ISource<T>, target: ITarget<T>, eventLimit: Int) =
        source.linkTo(target, LinkOptions(eventLimit = eventLimit))

/**
 * Builds a link between [source] and [target] specifying [predicate] (event filter)
 *
 * 构造链接
 */
fun <T> link(source: ISource<T>, target: ITarget<T>, predicate: (T) -> Boolean) =
        source.linkTo(target, LinkOptions(predicate))

/**
 * Builds a link between [source] and [target] specifying [options]
 *
 * 构造链接
 */
fun <T> link(source: ISource<T>, target: ITarget<T>, options: LinkOptions<T>) =
        source.linkTo(target, options)

/**
 * See [link]
 *
 * 构造链接
 */
operator fun <T> ISource<T>.minus(target: ITarget<T>) =
        linkTo(target)

/**
 * See [link]
 *
 * 构造链接
 */
operator fun <TIn, TOut> ISource<TIn>.minus(target: suspend (TIn) -> TOut) =
        StandardBlock(name = "transform",
                bufferSize = Int.MAX_VALUE,
                targetType = Normal,
                options = ExecutionOptions(),
                map = target
        ).also { linkTo(it) }

//-------------------------------
// build
//-------------------------------

/**
 * Builds an [ActionBlock]
 *
 * 构造 [ActionBlock] 节点
 */
fun <T> action(
        name: String = "action",
        options: ExecutionOptions = ExecutionOptions(),
        action: suspend (T) -> Unit
) = ActionBlock(name, options, action)

/**
 * Builds a broadcast block
 *
 * 构造基本广播节点
 */
fun <T> broadcast(name: String = "broadcast") =
        StandardBlock<T, T>(name = name,
                bufferSize = 1,
                targetType = Broadcast,
                options = ExecutionOptions(parallelismDegree = 1),
                map = { it })

/**
 * Builds a buffer block
 *
 * 构造基本缓存节点
 */
fun <T> buffer(name: String = "buffer",
               size: Int = Int.MAX_VALUE) =
        StandardBlock<T, T>(name = name,
                bufferSize = size,
                targetType = Normal,
                options = ExecutionOptions(parallelismDegree = 1),
                map = { it })

/**
 * Builds a transform block
 *
 * 构造转换节点
 */
fun <TIn, TOut> transform(name: String = "transform",
                          options: ExecutionOptions = ExecutionOptions(),
                          map: suspend (TIn) -> TOut) =
        StandardBlock(name = name,
                bufferSize = Int.MAX_VALUE,
                targetType = Normal,
                options = options,
                map = map)

/**
 * Builds a delay block
 *
 * 构造延时节点
 */
fun <T> delayBlock(name: String = "delay",
                   time: Long) =
        StandardBlock<T, T>(name = name,
                bufferSize = Int.MAX_VALUE,
                targetType = Normal,
                options = ExecutionOptions(parallelismDegree = Int.MAX_VALUE),
                map = { delay(time); it })
