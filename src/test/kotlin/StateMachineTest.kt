import org.mechdancer.dataflow.core.link
import org.mechdancer.dataflow.external.stateMachine.State
import org.mechdancer.dataflow.external.stateMachine.StateFlag
import org.mechdancer.dataflow.external.stateMachine.StateMachine

fun main(args: Array<String>) {
    val machine = StateMachine<Int>()
    val add = State(machine) { x -> x + 1 }
    val print = State(machine) { x -> x.also { println(it) } }
    link(add, print) { it.value < 1000 }
    link(add, machine.target)
    link(print, add)
    machine.startFrom(StateFlag(0, add))
    do {
        //Thread.sleep(100)
        //println(machine.running)
    } while (machine.running)
}
