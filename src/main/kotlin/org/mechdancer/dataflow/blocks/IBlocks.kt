package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*

/** 动作节点 */
interface IActionBlock<T> : ITarget<T>, IPostable<T>

/**
 * 广播节点
 *
 * 堆中的事件只会被新事件顶替，不会因为接收而消耗
 */
interface IBroadcastBlock<T> : IBridgeBlock<T>, IReceivable<T>, IPostable<T>

/**
 * 缓冲模块
 *
 * 未消耗的数据将保留，直到被消费
 */
interface IBufferBlock<T> : IBridgeBlock<T>, IReceivable<T>, IPostable<T>

/**
 * 延时模块
 *
 * 接收到消息后延迟指定的时间再发射
 */
interface IDelayBlock<T> : IBridgeBlock<T>, IReceivable<T>, IPostable<T>

/**
 * 定时模块
 *
 * 一个纯源模块，间隔指定的时间发射递增的长整型
 */
interface IIntervalBlock : ISource<Long>, IReceivable<Long>

/** 转换模块 */
interface ITransformBlock<TIn, TOut> : IPropagatorBlock<TIn, TOut>, IReceivable<TOut>, IPostable<TIn>