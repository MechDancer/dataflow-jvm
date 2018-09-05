package org.mechdancer.dataflow.core

/**
 * 源节点的定义
 * 需要管理对应的宿
 */
interface ISource<T> : IBlock, IEgress<T> {
	val targets: List<ITarget<T>>
	fun linkTo(target: ITarget<T>, options: LinkOptions<T>): ILink<T>
}
