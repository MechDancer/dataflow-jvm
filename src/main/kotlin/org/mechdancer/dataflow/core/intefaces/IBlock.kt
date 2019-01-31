package org.mechdancer.dataflow.core.intefaces

/**
 * 节点
 *
 * 有 `名字` 和 `UUID`
 */
interface IBlock : IWithUUID {
    val name: String
}
