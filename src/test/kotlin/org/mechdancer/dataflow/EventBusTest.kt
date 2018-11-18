//package org.mechdancer.dataflow
//
//import org.junit.Assert
//import org.junit.Test
//import org.mechdancer.dataflow.external.eventbus.EventBus
//import org.mechdancer.dataflow.external.eventbus.EventBus.Companion.DefaultExecutor
//import org.mechdancer.dataflow.external.eventbus.IEvent
//import org.mechdancer.dataflow.external.eventbus.annotations.Subscribe
//
//class EventBusTest {
//    @Test
//    fun test() {
//        val a = A()
//        val e = ShitEvent(233)
//        EventBus.getDefault().post(e)
//        Thread.sleep(100)
//        Assert.assertEquals(233, a.a)
//    }
//}
//
//data class ShitEvent(val id: Int) : IEvent
//
//class A {
//    var a = 1
//
//    init {
//        EventBus.getDefault().register(this)
//    }
//
//    @Subscribe(executor = DefaultExecutor)
//    fun onShit(e: ShitEvent) {
//        a = e.id
//    }
//}
