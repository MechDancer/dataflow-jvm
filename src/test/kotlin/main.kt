import site.syzk.dataflow.core.*

fun main(args: Array<String>) {
    val source = broadcast<Int>()
    val bridge1 = transform { x: Int -> x - 1 }
    val bridge2 = transform { x: Int -> -x }
    link(source, bridge1) { x -> x >= -100 }
    link(source, bridge2) { x -> x < -100 }
    source - { it > 0 } - { println(if (it) "+" else "-") }
    bridge1 linkTo source
    bridge2 linkTo source
    source post 100
//    while (true) {
//        readLine()
//        println("收到: ${source.receive()}")
//    }
    while (true);
}

