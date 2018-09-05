package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.core.IBlock
import java.util.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ScheduledThreadPoolExecutor

internal fun randomUUID(): UUID = UUID.randomUUID()

internal val defaultDispatcher = ForkJoinPool()

internal val scheduler = ScheduledThreadPoolExecutor(0)

internal fun Boolean.then(block: () -> Unit) = this.also { if (it) block() }

internal fun Boolean.otherwise(block: () -> Unit) = this.also { if (!it) block() }

internal fun IBlock.view() = "[$name][$uuid]"


