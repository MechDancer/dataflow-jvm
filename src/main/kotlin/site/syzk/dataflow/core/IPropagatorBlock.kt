package site.syzk.dataflow.core

interface IPropagatorBlock<TIn, TOut> : ITarget<TIn>, ISource<TOut>
