package org.mechdancer.dataflow

import org.junit.Assert
import org.junit.Test
import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.util.next
import org.mechdancer.dataflow.util.prior

class PureSourcesTest {
    companion object {
        val b1 = broadcast<Unit>("1")
        val b2 = broadcast<Unit>("2")
        val b3 = action<Unit>("3") { }
        val b4 = transform<Unit, Unit>("4") { }

        init {
            b1 - b3
            b1 - b4
            b2 - b4
            b4 - b2

            ILink.list.forEach(::println)
        }
    }

//    /** 测试查找纯源节点 */
//    @Test
//    fun findPureSources() {
//        val sources = ILink.list.pureSources()
//        Assert.assertEquals(1, sources.size)
//        Assert.assertEquals("1", sources.first().name)
//    }

    /** 测试查找前驱 */
    @Test
    fun findPrior() {
        val prior = b3.prior()
        Assert.assertEquals(1, prior.size)
        Assert.assertEquals("1", prior.first().name)
    }

    /** 测试查找后继 */
    @Test
    fun findNext() {
        val next = b4.next()
        Assert.assertEquals(1, next.size)
        Assert.assertEquals("2", next.first().name)
    }
}
