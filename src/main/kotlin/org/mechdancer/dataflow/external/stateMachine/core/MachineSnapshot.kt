package org.mechdancer.dataflow.external.stateMachine.core

data class MachineSnapshot<T>(
    val current: StateMember<T>,
    val value: T
)
