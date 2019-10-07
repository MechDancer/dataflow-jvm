package org.mechdancer.dataflow.core.intefaces

import org.mechdancer.dataflow.core.internal.SourceCore

/**
 * Represents a object is an event entry and can accept events from outside.
 *
 * Actually contains a implicit internal event exit.
 *
 * 可接受外部事件的实体
 *
 * 本质上包含一个隐含的内部出口节点
 */
interface IPostable<T> : IIngress<T> {
    /**
     * Default source
     *
     * 默认源节点
     */
    val defaultSource: DefaultSource<T>

    /**
     * Default source (virtue)
     *
     * Provides heap for events from outside.
     *
     * 默认源节点（虚拟源节点）
     * 为来自外部的事件提供堆
     */
    class DefaultSource<T>(private val owner: IPostable<T>) {
        private val core = SourceCore<T>(Int.MAX_VALUE)
        operator fun invoke(event: T) = owner.offer(core.offer(event), core)
    }
}
