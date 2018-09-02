package org.mechdancer.dataflow.core

import java.util.*

interface IWithUUID : Comparable<IWithUUID> {
	val uuid: UUID
	override fun compareTo(other: IWithUUID) =
		uuid.compareTo(other.uuid)
}
