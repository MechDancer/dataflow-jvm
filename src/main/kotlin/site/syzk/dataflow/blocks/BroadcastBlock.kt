package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.post

class BroadcastBlock<T> : ISource<T>, ITarget<T> {
    private val links = mutableListOf<ITarget<T>>()

    override fun consume(event: T) {
        links.forEach { it.post(event) }
    }

    override fun linkTo(target: ITarget<T>) {
        links.add(target)
    }
}
