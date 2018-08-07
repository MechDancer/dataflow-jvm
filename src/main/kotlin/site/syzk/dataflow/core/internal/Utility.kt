package site.syzk.dataflow.core.internal

internal fun Boolean.then(block: () -> Unit) {
    if (this) block()
}

internal fun Boolean.otherwise(block: () -> Unit) {
    if (!this) block()
}
