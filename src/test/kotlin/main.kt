import site.syzk.dataflow.blocks.ActionBlock
import site.syzk.dataflow.blocks.TransformBlock
import site.syzk.dataflow.core.post

fun main(args: Array<String>) {
    val source = TransformBlock<Int, Int> { it - 1 }
    source.linkTo(source)
    source.linkTo(ActionBlock { println(it) })
    source.post(100)
    readLine()
}
