package site.syzk.dataflow.core

/**
 * 默认源节点（虚拟源节点）
 * 为来自外部的事件提供堆
 */
class DefaultSource<T> : ISource<T> {
    private val core = SourceCore<T>()

    fun register(event: T) = core.register(event)
    override fun consume(id: Long) = core.consume(id)

    override fun linkTo(target: ITarget<T>) = throw Exception("这个方法没有任何用处")
}
