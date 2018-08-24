package org.mechdancer.dataflow.external.stateMachine

data class MachineState<T>(
    val current: State<T>,
    val value: T
)
