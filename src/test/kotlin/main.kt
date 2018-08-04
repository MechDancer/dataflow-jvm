import site.syzk.dataflow.blocks.ActionBlock
import site.syzk.dataflow.blocks.BroadcastBlock
import site.syzk.dataflow.blocks.TransformBlock
import site.syzk.dataflow.core.post

fun main(args: Array<String>) {
    val source = BroadcastBlock<Int>()
    val bridge = TransformBlock<Int, Int> {
        Thread.sleep(1000)
        it - 1
    }
    source.linkTo(bridge)
    source.linkTo(ActionBlock { println(it) })
    bridge.linkTo(source)
    source.post(100)
    readLine()
}