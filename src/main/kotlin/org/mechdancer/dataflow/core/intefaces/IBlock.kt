package org.mechdancer.dataflow.core.intefaces

/**
 * 块
 *
 * 数据流经的节点
 *
 * 用 [name] 和 [uuid] 区分
 */
interface IBlock : IWithUUID {
    val name: String
}
