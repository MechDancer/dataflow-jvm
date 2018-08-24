package org.mechdancer.dataflow.external.stateMachine.machines

import org.mechdancer.dataflow.core.link
import org.mechdancer.dataflow.core.post
import org.mechdancer.dataflow.external.stateMachine.core.StateMachine
import org.mechdancer.dataflow.external.stateMachine.core.StateMember

class StateList<T> {
    private val list = mutableListOf<Pair<Int, (T) -> T>>()

    fun once(action: (T) -> T) {
        list.add(1 to action)
    }

    fun loop(times: Int, action: (T) -> T) {
        assert(times > 0)
        list.add(times to action)
    }

    fun forever(action: (T) -> T) {
        list.add(Int.MAX_VALUE to action)
    }

    private infix fun Pair<Int, (T) -> T>.buildIn(
        machine: StateMachine<Pair<Int, T>>
    ) = StateMember(machine, first > 1) {
        it.first + 1 to second(it.second)
    }

    fun build(): (T) -> Unit {
        val machine = StateMachine<Pair<Int, T>>("linearStateMachine")
        val stateList = mutableListOf(list[0] buildIn machine)
        var stepCount = list[0].first
        for (i in 1..list.lastIndex) {
            stateList.add(list[i] buildIn machine)
            val x = stepCount
            link(stateList[i - 1], stateList[i]) { it.first >= x }
            stepCount = (stepCount + list[i].first).takeIf { it > 0 } ?: Int.MAX_VALUE
        }
        return {
            stateList.first() post (0 to it)
        }
    }
}

fun <T> linearStateMachine(block: StateList<T>.() -> Unit) =
    StateList<T>().apply(block).build()
