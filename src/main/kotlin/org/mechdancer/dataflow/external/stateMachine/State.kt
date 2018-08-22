package org.mechdancer.dataflow.external.stateMachine

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.internal.stub
import java.util.*
import kotlin.concurrent.thread

class State<T>(
    private val owner: StateMachine<T>,
    private val action: (T) -> T)
    : IPropagatorBlock<StateFlag<T>, StateFlag<T>> {
    override val name = "State"
    override val uuid = UUID.randomUUID()!!
    override val defaultSource by lazy { DefaultSource(this) }

    private val state get() = owner.current!!

    override fun offer(id: Long, link: Link<StateFlag<T>>): Feedback {
        thread {
            do {
                owner.current = StateFlag(action(state.value), this)
                val acceptable = owner.list
                    .filter { it.source === this && it.options.predicate(state) }
                if (acceptable.isEmpty()) continue
                acceptable.first().offer(0)
            } while (false)
        }
        return Feedback.Accepted
    }

    override fun consume(id: Long) = stub("这个方法暂时没用")

    override fun linkTo(target: ITarget<StateFlag<T>>, options: LinkOptions<StateFlag<T>>) =
        Link(this, target, options).also { owner.list.add(it) }
}
