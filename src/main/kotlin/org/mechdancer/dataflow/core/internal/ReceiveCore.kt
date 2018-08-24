package org.mechdancer.dataflow.core.internal

internal class ReceiveCore {
    private val receiveLock = Object()

    fun call() {
        synchronized(receiveLock) { receiveLock.notifyAll() }
    }

    private fun <T> get(block: () -> Pair<Boolean, T?>): T {
        while (true)
            block().let {
                if (it.first)
                    @Suppress("UNCHECKED_CAST")
                    return it.second as T
                else
                    synchronized(receiveLock) { receiveLock.wait() }
            }
    }

    infix fun <T> consumeFrom(sourceCore: SourceCore<T>) =
        get { sourceCore.consume() }

    infix fun <T> getFrom(sourceCore: SourceCore<T>) =
        get { sourceCore.get() }
}
