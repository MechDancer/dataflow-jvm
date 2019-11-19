package org.mechdancer.dataflow.core.intefaces

import org.mechdancer.dataflow.core.options.LinkOptions

/**
 * Source block
 *
 * Represents a block that is a source and exit for data.
 * Source shell manage corresponding targets.
 *
 * 源节点
 *
 * 需要管理对应的宿
 */
interface ISource<T> : IBlock, IEgress<T> {
    /**
     * Targets set
     *
     * 宿集合
     */
    val targets: Set<ITarget<T>>

    /**
     * Links to a specific target
     *
     * 添加到指定宿的链接
     */
    fun linkTo(target: ITarget<T>, options: LinkOptions<T> = LinkOptions()): ILink<T>
}
