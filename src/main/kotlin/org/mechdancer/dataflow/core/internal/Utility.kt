package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.core.intefaces.IBlock
import org.mechdancer.dataflow.core.intefaces.ILink
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.Lock

internal val scheduler = ScheduledThreadPoolExecutor(0)

internal fun IBlock.view() = "$name(${this::class.simpleName})"

internal val <T> LinkManager<T>.targets get() = set.map(ILink<T>::target).toSet()

internal fun Lock.withTryLock(block: () -> Unit) {
    if (tryLock()) try {
        block()
    } finally {
        unlock()
    }
}

internal fun AtomicInteger.increaseIf(block: (Int) -> Boolean): Boolean {
    var result = false
    updateAndGet {
        result = block(it)
        if (result) it + 1 else it
    }
    return result
}

internal fun AtomicLong.increaseIf(block: (Long) -> Boolean): Boolean {
    var result = false
    updateAndGet {
        result = block(it)
        if (result) it + 1 else it
    }
    return result
}
