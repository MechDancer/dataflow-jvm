package org.mechdancer.dataflow.util

import org.mechdancer.dataflow.core.intefaces.ILink
import org.mechdancer.dataflow.core.linkTo
import java.util.concurrent.ConcurrentSkipListSet

/**
 * 全局链接管理
 */
object LinkServer {
    val list by lazy {
        val temp = ConcurrentSkipListSet<ILink<*>>()
        ILink.changed linkTo { if (it.closed) temp.remove(it) else temp.add(it) }
        object : Collection<ILink<*>> by temp {}
    }

    fun init() {
        list
    }
}
