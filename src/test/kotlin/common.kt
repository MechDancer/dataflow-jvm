import site.syzk.dataflow.blocks.BufferBlock
import site.syzk.dataflow.core.post
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val buffer = BufferBlock<Int>("buffer")
    thread {
        var i = 0
        while (true) {
            buffer.post(i++)
            println("插入: $i, 计数: ${buffer.count}")
            Thread.sleep(500)
        }
    }
    while (true) {
        readLine()
        println("接收: ${buffer.receive()}")
    }
}
