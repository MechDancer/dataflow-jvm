package site.syzk.dataflow.core

interface ISource<T> {
    fun receive(timeout: Long): T

    fun linkTo(target: ITarget<T>)
}
