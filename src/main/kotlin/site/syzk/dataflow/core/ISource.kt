package site.syzk.dataflow.core

interface ISource<T> {
    fun receive(timeout: Long): Event<T>

    fun <TOut> linkTo(
            target: ITarget<TOut>,
            options: LinkOptions<T, TOut>? = null
    ): Link<T, TOut>
}
