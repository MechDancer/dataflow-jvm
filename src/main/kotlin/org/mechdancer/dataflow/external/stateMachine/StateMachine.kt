package org.mechdancer.dataflow.external.stateMachine

import org.mechdancer.dataflow.core.*
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet

class StateMachine<T> {
    val list = ConcurrentSkipListSet<Link<StateFlag<T>>>()
    var current: StateFlag<T>? = null

    val target = object : ITarget<StateFlag<T>> {
        override val name = "target"
        override val uuid = UUID.randomUUID()!!
        override val defaultSource by lazy { DefaultSource(this) }

        override fun offer(id: Long, link: Link<StateFlag<T>>): Feedback {
            current = null
            return Feedback.Accepted
        }
    }

    fun startFrom(state: StateFlag<T>) {
        current = state
        state.current post state
    }

    val running get() = current != null
}
