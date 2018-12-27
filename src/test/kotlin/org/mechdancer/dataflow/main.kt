package org.mechdancer.dataflow

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.util.treeView
import java.util.concurrent.atomic.AtomicInteger

/** 使用案例 */
fun main() {
    val source = broadcast<Int>("source")
    source - { delay(1); it - 1 } - source
    source - { delay(1); it + 1 } - source
    val link = source linkTo action { }

    source.treeView().let(::println)
    print("press enter to continue...")
    readLine()

    source post 100

    val count = AtomicInteger(0)
    GlobalScope.launch {
        while (true) {
            delay(1000)
            val last = count.getAndSet(link.count)
            println(count.get() - last)
        }
    }

    while (true) {
        readLine()
        println("收到: ${source.receive()}")
    }
}
