import org.junit.Assert
import org.junit.Test
import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.linkManage.next
import org.mechdancer.dataflow.linkManage.prior
import org.mechdancer.dataflow.linkManage.pureSources

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

			Link.user().forEach {
				println(it)
			}
		}
	}

	@Test
	fun findPureSources() {
		val sources = Link.user().pureSources()
		Assert.assertEquals(1, sources.size)
		Assert.assertEquals("1", sources.first().name)
	}

	@Test
	fun findPrior() {
		val prior = b3.prior()
		Assert.assertEquals(1, prior.size)
		Assert.assertEquals("1", prior.first().name)
	}

	@Test
	fun findNext() {
		val next = b4.next()
		Assert.assertEquals(1, next.size)
		Assert.assertEquals("2", next.first().name)
	}
}
