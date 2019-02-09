package org.mechdancer.dataflow.blocks

/**
 * 宿类型
 */
enum class TargetType {
    /** 广播模式：消息只会被新的消息覆盖，不会被消费 */
    Broadcast,
    /** 正常模式：消息正常到来，正常消费 */
    Normal,
    /** 冷淡模式：没有被接收的消息不会存储，立即丢弃 */
    Cold
}
