package org.mechdancer.dataflow

import org.junit.Assert
import org.junit.Test
import org.mechdancer.dataflow.core.delay
import org.mechdancer.dataflow.core.post

class DelayTest {
	/** 测试延时模块 */
	@Test
	fun test() {
		val delay = delay<Int>(2000)
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

