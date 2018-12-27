package org.mechdancer.dataflow.external.eventbus.annotations

import org.mechdancer.dataflow.external.eventbus.EventBus

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
/**
 * 订阅事件
 *
 * 标记在一个类中的函数上，在该类中使用 [EventBus.getDefault.register()]
 *
 * @param sticky 是否接收粘性事件
 * @param executor 执行该函数调用的调度器
 **/
annotation class Subscribe(
    val sticky: Boolean = false,
    val executor: String = EventBus.DefaultExecutor
)
