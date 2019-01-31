package org.mechdancer.dataflow.core.intefaces

/**
 * 宿节点
 */
interface ITarget<T> : IBlock, IIngress<T>

/** 入口节点 */
interface IEntranceBlock<T> : ITarget<T>,
                              IPostable<T>

/** 出口节点 */
interface IExitBlock<T> : ISource<T>,
                          IReceivable<T>

/** 传递节点 */
interface IPropagatorBlock<TIn, TOut> : ITarget<TIn>,
                                        ISource<TOut>

/** 桥接模块 := 不改变事件类型的传递模块 */
typealias IBridgeBlock<T> = IPropagatorBlock<T, T>

/** 大全套节点 */
interface IFullyBlock<TIn, TOut> : IEntranceBlock<TIn>,
                                   IExitBlock<TOut>
