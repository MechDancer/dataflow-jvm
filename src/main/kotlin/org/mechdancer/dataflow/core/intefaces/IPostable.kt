package org.mechdancer.dataflow.core.intefaces

import org.mechdancer.dataflow.core.internal.SourceCore

/**
 * 可接受外部事件的实体
 *
 * 本质上包含一个隐含的内部出口节点
 */
interface IPostable<T> : IIngress<T> {

    /**
     * 默认源节点
     */
    val defaultSource: DefaultSource<T>

    /**
     * 默认源节点（虚拟源节点）
     * 为来自外部的事件提供堆
     */
    class DefaultSource<T>(private val owner: IPostable<T>) {
        private val core = SourceCore<T>(Int.MAX_VALUE)
        operator fun invoke(event: T) = owner.offer(core.offer(event), core)
    }
}
