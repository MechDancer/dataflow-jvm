package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.IEgress
import org.mechdancer.dataflow.core.IPostable.DefaultSource
import org.mechdancer.dataflow.core.ITarget
import org.mechdancer.dataflow.core.LinkOptions
import org.mechdancer.dataflow.core.internal.*
import org.mechdancer.dataflow.core.message


class BroadcastBlock<T>(
        override val name: String = "broadcast",
        private val clone: ((T) -> T)? = null
) : IBroadcastBlock<T> {
    private val linkManager = LinkManager(this)
    private val receiveCore = ReceiveCore()
    private val sourceCore = SourceCore<T>(1)
    private val targetCore = TargetCore<T> { event ->
        linkManager.offer(sourceCore.offer(event), event)
        receiveCore.call()
    }

    override val uuid = randomUUID()
    override val defaultSource by lazy { DefaultSource(this) }
    override val targets get() = linkManager.targets

    override fun offer(id: Long, egress: IEgress<T>) = targetCore.offer(id, egress)
    override fun receive() = receiveCore getFrom sourceCore
    override fun consume(id: Long) =
            sourceCore[id].let {
                if (it.hasValue && clone != null) message(clone.invoke(it.value))
                else it
            }

    override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) =
            linkManager.linkTo(target, options)

    override fun toString() = view()
}
