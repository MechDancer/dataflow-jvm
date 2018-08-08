import site.syzk.dataflow.blocks.ActionBlock
import site.syzk.dataflow.blocks.BroadcastBlock
import site.syzk.dataflow.blocks.TransformBlock
import site.syzk.dataflow.core.executableOpotions
import site.syzk.dataflow.core.linkOptions
import site.syzk.dataflow.core.post

fun main(args: Array<String>) {
    val source = BroadcastBlock<Int>()
    val bridge = TransformBlock<Int, Int>(
            executableOpotions(1)) { it - 1 }
    source.linkTo(bridge)
    source.linkTo(ActionBlock {
        println("[${System.nanoTime() / 1000000 % 100000}][$it]")
    })
    bridge.linkTo(source, linkOptions(20))
    source.post(100)
    while (true) {
        readLine()
        println("收到2: ${source.receive()}")
    }
}

