package org.mechdancer.dataflow.core

/**
 * 源节点的定义
 * 需要管理对应的宿
 */
interface ISource<T> : IBlock, IEgress<T> {
    val targets: Set<ITarget<T>>

	/** 添加到指定宿的链接 */
	fun linkTo(target: ITarget<T>, options: LinkOptions<T> = LinkOptions()): ILink<T>
}
