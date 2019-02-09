package org.mechdancer.dataflow.core

import org.mechdancer.dataflow.core.intefaces.ILink
import java.util.concurrent.ConcurrentSkipListSet

object LinkServer {
    val list: Collection<ILink<*>>  by lazy {
        val temp = ConcurrentSkipListSet<ILink<*>>()
        ILink.changed linkTo {
            if (it.closed) temp.remove(it)
            else temp.add(it)
        }
        object : Collection<ILink<*>> by temp {}
    }

    fun start() {
        list
    }
}
