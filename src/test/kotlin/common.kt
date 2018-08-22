import org.mechdancer.dataflow.blocks.SubNetBlock
import org.mechdancer.dataflow.blocks.SubNetBlock.LinkInfo
import org.mechdancer.dataflow.core.broadcast
import org.mechdancer.dataflow.core.linkTo
import org.mechdancer.dataflow.core.transform
import org.mechdancer.dataflow.linkManage.treeView

fun main(args: Array<String>) {
    val source = broadcast<Int>()
    val bridge1 = transform { x: Int -> x - 1 }
    val bridge2 = transform { x: Int -> -x }
    val sub = SubNetBlock(
        i = source, o = source,
        links = listOf(
            LinkInfo(source, bridge1),
            LinkInfo(source, bridge2),
            LinkInfo(bridge1, source),
            LinkInfo(bridge2, source)
        )
    )
    sub linkTo { println(it) }
    //sub post 0

    println(sub.treeView())
    while (true);
}
