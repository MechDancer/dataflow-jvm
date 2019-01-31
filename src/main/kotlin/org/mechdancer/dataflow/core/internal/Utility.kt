package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.core.intefaces.IBlock
import org.mechdancer.dataflow.core.intefaces.ILink
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor

internal fun randomUUID(): UUID = UUID.randomUUID()

internal val scheduler = ScheduledThreadPoolExecutor(0)

internal fun IBlock.view() = "$name(${this::class.simpleName})"

internal val <T> LinkManager<T>.targets get() = set.map(ILink<T>::target).toSet()
