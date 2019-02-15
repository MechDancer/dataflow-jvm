package org.mechdancer.dataflow.core.intefaces

/** 可接收模块 */
interface IReceivable<T> {
    /** 阻塞接收 */
    suspend fun receive(): T
}
