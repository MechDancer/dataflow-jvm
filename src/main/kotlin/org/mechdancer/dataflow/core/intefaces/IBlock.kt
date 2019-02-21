package org.mechdancer.dataflow.core.intefaces

/**
 * 块
 *
 * 有 [name] 和 [uuid]
 */
interface IBlock : IWithUUID {

    val name: String

}
