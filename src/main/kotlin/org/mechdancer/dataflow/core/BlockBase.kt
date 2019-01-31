package org.mechdancer.dataflow.core

import org.mechdancer.dataflow.core.intefaces.IBlock
import org.mechdancer.dataflow.core.intefaces.IWithUUID
import java.util.*

class BlockBase(override val name: String)
    : IBlock, IWithUUID by UUIDBase() {
    override val uuid: UUID = UUID.randomUUID()
    override fun toString() = "$name(${this::class.simpleName})"
}
