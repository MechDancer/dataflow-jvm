package org.mechdancer.dataflow

import org.junit.Test
import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.util.treeView

class LinkManageTest {
	/**
	 * 测试链接管理
	 * 树状图只能人看
	 */

	@Test
	fun test() {
		val lock = Object()
		ILink.changed linkTo { list ->
			synchronized(lock) {
				println(list.size)
				list.forEach { println(it) }
				println()
			}
		}

		val source = broadcast<Int>()
		val bridge1 = transform { x: Int -> x - 1 }
		val bridge2 = transform { x: Int -> -x }
		val begin = System.currentTimeMillis()
		val link = link(source, bridge1)
		link(source, bridge2)
		source - { it > 0 } - { println(if (it) "+" else "-") }
		bridge1 linkTo source
		bridge2 linkTo source
		source linkTo { println(link.count / (System.currentTimeMillis() - begin)) }

		ILink.all().forEach { println(it) }
		println(source.treeView())
	}
}

