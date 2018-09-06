import org.junit.Test
import org.mechdancer.dataflow.core.delay
import org.mechdancer.dataflow.core.post

class DelayTest {
	@Test
	fun test() {
		val delay = delay<Int>(2000)
		delay post 10
		println(delay.receive())
	}
}

