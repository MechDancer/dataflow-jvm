package org.mechdancer.dataflow.core

/**
 * 节点
 * 有名字和唯一ID
 */
interface IBlock : IWithUUID {
    val name: String
}
