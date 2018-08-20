import org.mechdancer.dataflow.core.broadcast
import org.mechdancer.dataflow.core.minus
import org.mechdancer.dataflow.core.post

fun main(args: Array<String>) {
    val source = broadcast<Int>("源")
    source - { it - 1 } - source
    source - { it + 1 } - source
    source - { println(it) }
    source post 100
    while (true) {
        readLine()
        println("收到: ${source.receive()}")
    }
}
