package org.mechdancer.dataflow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.util.treeView
import java.util.concurrent.atomic.AtomicInteger

/** 使用案例 */
fun main() {
    val source = broadcast<Int>("source")
    source - { println(it) }

    source.treeView().let(::println)
    print("press enter to continue...")
    readLine()

    runBlocking(Dispatchers.Default) {
        launch {
            for (i in 0..Int.MAX_VALUE) {
                source post i
                delay(200L)
            }
        }
        while (true) {
            readLine()
            println("收到: ${source.receive()}")
        }
    }
}
