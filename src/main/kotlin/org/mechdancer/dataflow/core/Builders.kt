package org.mechdancer.dataflow.core

import kotlinx.coroutines.delay
import org.mechdancer.dataflow.blocks.ActionBlock
import org.mechdancer.dataflow.blocks.StandardBlock
import org.mechdancer.dataflow.blocks.TargetType.Broadcast
import org.mechdancer.dataflow.blocks.TargetType.Normal
import org.mechdancer.dataflow.core.intefaces.IPostable
import org.mechdancer.dataflow.core.intefaces.ISource
import org.mechdancer.dataflow.core.intefaces.ITarget
import org.mechdancer.dataflow.core.options.ExecutableOptions
import org.mechdancer.dataflow.core.options.LinkOptions

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
infix fun <T> ISource<T>.linkTo(target: suspend (T) -> Unit) =
    linkTo(ActionBlock(action = target))

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
operator fun <TIn, TOut> ISource<TIn>.minus(target: suspend (TIn) -> TOut) =
    StandardBlock(name = "transform",
                  bufferSize = Int.MAX_VALUE,
                  targetType = Normal,
                  options = ExecutableOptions(),
                  map = target
    ).also { linkTo(it) }

//-------------------------------
// build
//-------------------------------

/** 构造 [ActionBlock] 节点 */
fun <T> action(
    name: String = "action",
    options: ExecutableOptions = ExecutableOptions(),
    action: suspend (T) -> Unit
) = ActionBlock(name, options, action)

/** 构造基本广播节点节点 */
fun <T> broadcast(name: String = "broadcast") =
    StandardBlock<T, T>(name = name,
                        bufferSize = 1,
                        targetType = Broadcast,
                        options = ExecutableOptions(parallelismDegree = 1),
                        map = { it })

/** 构造基本缓存节点节点 */
fun <T> buffer(name: String = "buffer",
               size: Int = Int.MAX_VALUE) =
    StandardBlock<T, T>(name = name,
                        bufferSize = size,
                        targetType = Normal,
                        options = ExecutableOptions(parallelismDegree = 1),
                        map = { it })

/** 构造转换节点 */
fun <TIn, TOut> transform(name: String = "transform",
                          options: ExecutableOptions = ExecutableOptions(),
                          map: suspend (TIn) -> TOut) =
    StandardBlock(name = name,
                  bufferSize = Int.MAX_VALUE,
                  targetType = Normal,
                  options = options,
                  map = map)

/** 构造延时节点 */
fun <T> delayBlock(name: String = "delay",
                   time: Long) =
    StandardBlock<T, T>(name = name,
                        bufferSize = Int.MAX_VALUE,
                        targetType = Normal,
                        options = ExecutableOptions(parallelismDegree = Int.MAX_VALUE),
                        map = { delay(time); it })
