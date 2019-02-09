package org.mechdancer.dataflow

import org.junit.Test
import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.intefaces.ILink
import org.mechdancer.dataflow.util.treeView
import java.util.concurrent.ConcurrentSkipListSet

class LinkManageTest {
    /**
     * 测试链接管理
     * 树状图只能人看
     */
    @Test
    fun test() {
        val list = ConcurrentSkipListSet<ILink<*>>()
        LinkServer.changed linkTo { list.add(it) }

        val source = broadcast<Int>("source")
        val bridge1 = transform("bridge1") { x: Int -> x - 1 }
        val bridge2 = transform("bridge2") { x: Int -> -x }
        val begin = System.currentTimeMillis()

        val link = link(source, bridge1)
        link(source, bridge2)
        source - { it > 0 } - { println(if (it) "+" else "-") }
        bridge1 linkTo source
        bridge2 linkTo source
        source linkTo { println(link.count / (System.currentTimeMillis() - begin)) }

        Thread.sleep(1000)
        println("--------------------------------")
        list.forEach(::println)
        println("--------------------------------")
        println(source.treeView())
    }
}

