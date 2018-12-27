package org.mechdancer.dataflow.core

/**
 * 事件出口
 *
 * 可从中消费事件的端口
 */
interface IEgress<T> {
    /**
     * 消费一个事件
     *
     * 如果成功，事件可能从源的队列中移除
     * 由得到源通知的宿调用
     * @param id 事件的标识
     * @return 事件消息
     */
    infix fun consume(id: Long): Message<out T>
}
