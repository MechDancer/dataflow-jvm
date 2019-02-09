package org.mechdancer.dataflow

import org.junit.Assert
import org.junit.Test
import org.mechdancer.dataflow.core.action
import org.mechdancer.dataflow.core.broadcast
import org.mechdancer.dataflow.core.minus
import org.mechdancer.dataflow.core.transform
import org.mechdancer.dataflow.util.LinkServer
import org.mechdancer.dataflow.util.pureSources
import org.mechdancer.dataflow.util.pureTargets

class PureSourcesTest {
    companion object {
        init {
            val b1 = broadcast<Unit>("1")
            val b2 = broadcast<Unit>("2")
            val b3 = action<Unit>("3") { }
            val b4 = transform<Unit, Unit>("4") { }

            LinkServer.init()

            b1 - b3
            b1 - b4
            b2 - b4
            b4 - b2

            Thread.sleep(30)
            LinkServer.list.forEach(::println)
        }
    }

    /** 测试查找纯源节点 */
    @Test
    fun findPureSources() {
        val sources = LinkServer.list.pureSources()
        Assert.assertEquals(1, sources.size)
        Assert.assertEquals("1", sources.first().name)
    }

    /** 测试查找纯宿节点 */
    @Test
    fun findPureTargets() {
        val sources = LinkServer.list.pureTargets()
        Assert.assertEquals(1, sources.size)
        Assert.assertEquals("3", sources.first().name)
    }
}
