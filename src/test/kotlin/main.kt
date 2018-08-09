import site.syzk.dataflow.core.internal.broadcast
import site.syzk.dataflow.core.internal.minus
import site.syzk.dataflow.core.internal.post
import site.syzk.dataflow.core.internal.transform

fun main(args: Array<String>) {
    val source = broadcast<Int>()
    val bridge1 = transform<Int, Int> { it - 1 }
    val bridge2 = transform<Int, Int> { -it }
    source - bridge1
    source - bridge2
    source - { println("[${System.currentTimeMillis()}][$it]") }
    bridge1 - source
    //bridge2 - source
    source post 100
    while (true) {
        readLine()
        println("收到: ${source.receive()}")
    }
}

