package site.syzk.dataflow.core

/**
 * 事件
 * @param begin 从第一个源产生的时间（毫微秒）
 * @param value 当前值
 */
data class Event<T>(val begin: Long, val value: T)

/**
 * 产生事件
 */
fun <T> event(value: T) = Event(System.nanoTime(), value)
