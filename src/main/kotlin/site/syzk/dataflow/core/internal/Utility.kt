package site.syzk.dataflow.core.internal

internal fun Boolean.then(block: () -> Unit): Boolean {
	if (this) block()
	return this
}

internal fun Boolean.otherwise(block: () -> Unit): Boolean {
	if (!this) block()
	return this
}

internal fun <T> Boolean.zip(block: () -> T?) =
		this to if (this) block() else null

internal fun stub(message: String): Nothing = throw UnsupportedOperationException(message)
