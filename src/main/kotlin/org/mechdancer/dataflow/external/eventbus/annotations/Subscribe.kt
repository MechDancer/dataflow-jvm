package org.mechdancer.dataflow.external.eventbus.annotations

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Subscribe(val sticky: Boolean = false)