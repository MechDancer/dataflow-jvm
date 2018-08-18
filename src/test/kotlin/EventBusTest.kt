import org.junit.Test
import org.mechdancer.dataflow.external.eventbus.EventBus
import org.mechdancer.dataflow.external.eventbus.IEvent
import org.mechdancer.dataflow.external.eventbus.annotations.Subscribe

class EventBusTest {
	@Test
	fun test() {
		val e = ShitEvent(233)
		EventBus.getDefault.postSticky(e)
		val a = A()
		Thread.sleep(100)
		println(a.a)

	}
}

data class ShitEvent(val id: Int) : IEvent

class A {
	var a = 1

	init {
		EventBus.getDefault.register(this)
	}

	@Subscribe(sticky = true)
	fun onShit(e: IEvent) {
		when (e) {
			is ShitEvent -> a = e.id
		}
	}
}