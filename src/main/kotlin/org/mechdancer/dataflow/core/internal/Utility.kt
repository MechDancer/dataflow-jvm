package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.core.IBlock

internal fun Boolean.then(block: () -> Unit) = this.also { if (it) block() }

internal fun Boolean.otherwise(block: () -> Unit) = this.also { if (!it) block() }

internal fun IBlock.view() = "[$name][$uuid]"
