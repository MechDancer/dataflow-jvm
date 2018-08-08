import site.syzk.dataflow.blocks.ActionBlock
import site.syzk.dataflow.blocks.BroadcastBlock
import site.syzk.dataflow.blocks.TransformBlock
import site.syzk.dataflow.core.executableOptions
import site.syzk.dataflow.core.post

fun main(args: Array<String>) {
    val source = BroadcastBlock<Int>("source")
    val bridge1 = TransformBlock<Int, Int>(
            "bridge1", executableOptions(2)) {
        it - 1
    }
    val bridge2 = TransformBlock<Int, Int>(
            "bridge2", executableOptions(2)) {
        -it
    }
    source.linkTo(bridge1)
    source.linkTo(bridge2)
    source.linkTo(ActionBlock("out", executableOptions(1)) {
        println("[${System.currentTimeMillis()}][$it]")
    })
    bridge1.linkTo(source)
    bridge2.linkTo(source)
    source.post(100)
    while (true) {
        readLine()
        println("收到: ${source.receive()}")
    }
}

