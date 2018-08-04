package site.syzk.dataflow.core

import kotlin.concurrent.thread

interface ITarget<T> {
    fun consume(event: T)
}

fun <T> ITarget<T>.post(event: T) = thread { consume(event) }
