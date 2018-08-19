import org.junit.Test
import org.mechdancer.dataflow.external.eventbus.EventBus
import org.mechdancer.dataflow.external.eventbus.EventBusImpl
import org.mechdancer.dataflow.external.eventbus.IEvent
import org.mechdancer.dataflow.external.eventbus.annotations.Subscribe
import java.util.concurrent.ForkJoinPool

class EventBusTest {
	@Test
	fun test() {

		val a = A()
		val e = ShitEvent(233)

		(EventBus.getDefault() as EventBusImpl).setDispatcher(A::onShit, ForkJoinPool.commonPool())

		EventBus.getDefault().post(e)
		Thread.sleep(100)
		println(a.a)

	}
}

data class ShitEvent(val id: Int) : IEvent

class A {
	var a = 1

	init {
		EventBus.getDefault().register(this)
	}

	@Subscribe
	fun onShit(e: ShitEvent) {
		a = e.id
	}
}