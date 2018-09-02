package org.mechdancer.dataflow.core

interface ISource<T> : IBlock, IEgress<T> {
	val targets: List<ITarget<T>>
	fun linkTo(target: ITarget<T>, options: LinkOptions<T>): ILink<T>
}
