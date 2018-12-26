package org.mechdancer.dataflow

import org.junit.Assert
import org.junit.Test
import org.mechdancer.dataflow.blocks.DelayBlock
import org.mechdancer.dataflow.core.post
import java.util.concurrent.TimeUnit

class DelayTest {
    /** 测试延时模块 */
    @Test
    fun test() {
        val delay = DelayBlock<Int>(delay = 2000, unit = TimeUnit.MILLISECONDS)
        delay post 10
        System.nanoTime()
                .also { delay.receive() }
                .let {
                    val time = (System.nanoTime() - it) / 1E9
                    println(time)
                    Assert.assertEquals(2.0, time, 0.1)
                }
    }
}

