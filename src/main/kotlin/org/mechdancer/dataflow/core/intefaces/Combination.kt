package org.mechdancer.dataflow.core.intefaces

/**
 * Target block
 *
 * Represents a block that is a target for data.
 *
 * 宿节点
 */
interface ITarget<T> : IBlock, IIngress<T>

/**
 * Entry block
 *
 * Represents a block that is a entry and target for data,
 * which has a virtue core supporting posting events.
 *
 * 入口节点
 */
interface IEntranceBlock<T> : ITarget<T>,
                              IPostable<T>

/**
 * Exit block
 *
 * Represents a block that is a source for data.
 *
 * 出口节点
 */
interface IExitBlock<T> : ISource<T>,
                          IReceivable<T>

/**
 * Transferring node
 *
 * 传递节点
 */
interface IPropagatorBlock<TIn, TOut> : ITarget<TIn>,
                                        ISource<TOut>

/**
 * Bridging node
 * 桥接模块 := 不改变事件类型的传递模块
 */
typealias IBridgeBlock<T> = IPropagatorBlock<T, T>

/** 大全套节点 */
interface IFullyBlock<TIn, TOut> : IEntranceBlock<TIn>,
                                   IExitBlock<TOut>
