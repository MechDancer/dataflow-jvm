package org.mechdancer.dataflow.core

interface ITarget<T> : IBlock, IIngress<T> {
	/**
	 * 默认源
	 * 储存来自外部的事件
	 */
	val defaultSource: DefaultSource<T>
}


