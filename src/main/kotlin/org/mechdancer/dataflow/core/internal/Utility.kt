package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.core.IBlock
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor

internal fun randomUUID(): UUID = UUID.randomUUID()

internal val scheduler = ScheduledThreadPoolExecutor(0)

internal fun IBlock.view() = "[$name][$uuid]"


