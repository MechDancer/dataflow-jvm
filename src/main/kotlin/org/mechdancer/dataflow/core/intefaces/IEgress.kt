package org.mechdancer.dataflow.core.intefaces

import org.mechdancer.common.extension.Optional

/**
 * Event exit
 *
 * Can consume a event.
 *
 * 事件出口
 *
 * 可从中消费事件的端口
 */
interface IEgress<T> {
    /**
     * Consumes a event
     *
     * If is success, event might be removed from queue in source.
     * Called by target, which notified by source.
     *
     * 消费一个事件
     *
     * 如果成功，事件可能从源的队列中移除
     * 由得到源通知的宿调用
     *
     * @param id id of the event 事件的标识
     * @return event message 事件消息
     */
    infix fun consume(id: Long): Optional<T>
}
