package site.syzk.dataflow.core

interface ISource<T> {
    fun linkTo(target: ITarget<T>)
}
