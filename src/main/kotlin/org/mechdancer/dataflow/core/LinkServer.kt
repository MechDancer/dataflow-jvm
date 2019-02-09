package org.mechdancer.dataflow.core

import org.mechdancer.dataflow.core.intefaces.ILink
import java.util.concurrent.ConcurrentSkipListSet

object LinkServer {
    val changed = broadcast<ILink<*>>()

    val list: Collection<ILink<*>>  by lazy {
        val temp = ConcurrentSkipListSet<ILink<*>>()
        changed linkTo {
            if (it.closed) temp.remove(it)
            else temp.add(it)
        }
        object : Collection<ILink<*>> by temp {}
    }

    init {
        ILink.changed linkTo changed
    }
}
