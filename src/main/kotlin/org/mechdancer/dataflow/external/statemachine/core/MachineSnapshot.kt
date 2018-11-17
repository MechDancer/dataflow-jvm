package org.mechdancer.dataflow.external.statemachine.core

data class MachineSnapshot<T>(
        val current: StateMember<T>,
        val value: T
)
