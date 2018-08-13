package org.mechdancer.dataflow.core

interface IPropagatorBlock<TIn, TOut> : ITarget<TIn>, ISource<TOut>
