import org.mechdancer.dataflow.core.delay
import org.mechdancer.dataflow.core.post

fun main(args: Array<String>) {
    val delay = delay<Int>(2000)
    delay post 10
    println(delay.receive())
}
