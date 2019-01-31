package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.core.Message

internal class ReceiveCore {
    private val receiveLock = Object()

    fun call() {
        synchronized(receiveLock) { receiveLock.notifyAll() }
    }

    private fun <T> get(block: () -> Message<out T>): T {
        while (true)
            block().let {
                if (it.hasValue) return it.value
                else synchronized(receiveLock) { receiveLock.wait() }
            }
    }

    infix fun <T> consumeFrom(sourceCore: SourceCore<T>) =
        get { sourceCore.consume() }

    infix fun <T> getFrom(sourceCore: SourceCore<T>) =
        get { sourceCore.get() }
}
