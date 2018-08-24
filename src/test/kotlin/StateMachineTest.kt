import org.mechdancer.dataflow.core.link
import org.mechdancer.dataflow.core.post
import org.mechdancer.dataflow.external.stateMachine.State
import org.mechdancer.dataflow.external.stateMachine.StateMachine

fun main(args: Array<String>) {
    val machine = StateMachine<Int>("for")
    val add = State(machine) { x -> x + 1 }
    val print = State(machine) { x -> x.also { println(it) } }
    link(add, print) { it < 1000 }
    link(add, machine.ending) { it >= 1000 }
    link(print, add)
    print post 0
    while (!machine.running);
}
