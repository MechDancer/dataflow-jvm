package org.mechdancer.dataflow.core.internal

import org.mechdancer.common.extension.Optional

internal class ReceiveCore {
    private val receiveLock = Object()

    fun call() {
        synchronized(receiveLock) { receiveLock.notifyAll() }
    }

    private fun <T> get(block: () -> Optional<T>): T {
        while (true) {
            block().then { return it }
            synchronized(receiveLock) { receiveLock.wait() }
        }
    }

    infix fun <T> consumeFrom(sourceCore: SourceCore<T>) =
        get { sourceCore.consume() }

    infix fun <T> getFrom(sourceCore: SourceCore<T>) =
        get { sourceCore.get() }
}
