package site.syzk.dataflow.core

data class LinkOptions<TIn, TOut>(
        val filter: ((TIn) -> Boolean)?,
        val transformer: (TIn) -> TOut,
        val counter: ((Int) -> Boolean)?
)
