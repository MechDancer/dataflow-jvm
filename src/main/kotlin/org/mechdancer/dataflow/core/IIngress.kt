package org.mechdancer.dataflow.core

interface IIngress<T> {
	/**
	 * 通知目标节点，链接了该节点的某个源有事件到来
	 * 由链接节点的源调用
	 * @param id 源内部事件的唯一标识，供节点查找
	 * @param egress 事件到来的出口
	 * @return 入口对事件的态度
	 */
	fun offer(id: Long, egress: IEgress<T>): Feedback
}
