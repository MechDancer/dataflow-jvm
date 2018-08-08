import site.syzk.dataflow.blocks.ActionBlock
import site.syzk.dataflow.blocks.BroadcastBlock
import site.syzk.dataflow.blocks.TransformBlock
import site.syzk.dataflow.core.executableOpotions
import site.syzk.dataflow.core.post

fun main(args: Array<String>) {
    val source = BroadcastBlock<Int>()
    val bridge1 = TransformBlock<Int, Int>(
            executableOpotions(1)) { it - 1 }
    val bridge2 = TransformBlock<Int, Int>(
            executableOpotions(1)) { -it }
    source.linkTo(bridge1)
    source.linkTo(bridge2)
    source.linkTo(ActionBlock {
        println("[${System.nanoTime() / 1000000 % 100000}][$it]")
    })
    bridge1.linkTo(source)
    bridge2.linkTo(source)
    source.post(100)
    while (true) {
        readLine()
        println("收到: ${source.receive()}")
    }
}

