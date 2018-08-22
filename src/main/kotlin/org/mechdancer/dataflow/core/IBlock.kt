package org.mechdancer.dataflow.core

import java.util.*

/**
 * 模块
 * 定义模块标识符
 */
interface IBlock {
    val name: String
    val uuid: UUID
}
