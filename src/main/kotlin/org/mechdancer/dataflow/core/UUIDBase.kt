package org.mechdancer.dataflow.core

import org.mechdancer.dataflow.core.intefaces.IWithUUID
import java.util.*

internal class UUIDBase : IWithUUID {
    override val uuid: UUID = UUID.randomUUID()
    override fun compareTo(other: IWithUUID) = uuid.compareTo(other.uuid)
}
