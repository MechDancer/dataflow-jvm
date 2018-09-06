package org.mechdancer.dataflow.core

/** 传递节点 */
interface IPropagatorBlock<TIn, TOut> : ITarget<TIn>, ISource<TOut>
