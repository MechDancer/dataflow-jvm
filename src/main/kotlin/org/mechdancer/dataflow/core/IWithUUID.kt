package org.mechdancer.dataflow.core

import java.util.*

/**
 * 有 `UUID` 的实例
 *
 * 即可供人类区分、可存于集合的实体
 */
interface IWithUUID : Comparable<IWithUUID> {
    val uuid: UUID
    override fun compareTo(other: IWithUUID) =
            uuid.compareTo(other.uuid)
}
