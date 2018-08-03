package core

import core.annotations.ThreadSafety

/**
 * 发送器
 * 用于从源向链接转发
 */
@ThreadSafety(true)
class Poster<T> {
    private val targets = mutableListOf<Link<T, *>>()

    /**
     * 尝试向所有链接发送事件
     */
    fun post(event: Event<T>): Boolean = TODO()
}
