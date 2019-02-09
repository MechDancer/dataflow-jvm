package org.mechdancer.dataflow.core.intefaces

/**
 * Represents this object can [receive].
 *
 * 可接收模块
 */
interface IReceivable<T> {
    /**
     * Blocking receive
     *
     * 阻塞接收
     */
    fun receive(): T
}
