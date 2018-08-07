package site.syzk.dataflow.core.internal

import site.syzk.dataflow.annotations.ThreadSafety
import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.Link

@ThreadSafety(true)
internal class LinkManager<T>(private val owner: ISource<T>) {
    private val _targets = mutableListOf<ITarget<T>>()

    /**
     * 宿节点列表
     */
    val targets get() = _targets.toList()

    /**
     * 添加链接
     */
    fun linkTo(target: ITarget<T>): Link<T> {
        synchronized(target) { _targets.add(target) }
        return Link(owner, target)
    }

    /**
     * 取消链接
     */
    fun unlink(target: ITarget<T>) {
        synchronized(target) { _targets.remove(target) }
    }
}
