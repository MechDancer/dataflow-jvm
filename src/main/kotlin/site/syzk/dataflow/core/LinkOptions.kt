package site.syzk.dataflow.core

data class LinkOptions<T>
internal constructor(
		val predicate: (T) -> Boolean,
		val eventLimit: Int
)

fun <T> linkOptions() = LinkOptions<T>({ true }, Int.MAX_VALUE)
fun <T> linkOptions(predicate: (T) -> Boolean) = LinkOptions(predicate, Int.MAX_VALUE)
fun <T> linkOptions(eventLimit: Int) = LinkOptions<T>({ true }, eventLimit)
fun <T> linkOptions(predicate: (T) -> Boolean, eventLimit: Int) = LinkOptions(predicate, eventLimit)
