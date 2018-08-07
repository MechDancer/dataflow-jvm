package site.syzk.dataflow.core

fun Boolean.then(block: () -> Unit) {
    if (this) block()
}

fun Boolean.otherwise(block: () -> Unit) {
    if (!this) block()
}
