import site.syzk.dataflow.core.*

fun main(args: Array<String>) {
    val source = broadcast<Int>()
    val bridge1 = transform<Int, Int> { it - 1 }
    val bridge2 = transform<Int, Int> { -it }
    link(source, bridge1) { x -> x >= -100 }
    link(source, bridge2) { x -> x < -100 }
    source linkTo { println("[${System.currentTimeMillis()}][$it]") }
    bridge1 linkTo source
    bridge2 linkTo source
    source post 100
    while (true) {
        readLine()
        println("收到: ${source.receive()}")
    }
}

