package org.mechdancer.dataflow.external.stateMachine

data class StateFlag<T>(
    val value: T,
    val current: State<T>
)
