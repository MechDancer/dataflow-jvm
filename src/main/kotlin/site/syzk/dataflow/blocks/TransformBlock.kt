package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.*

class TransformBlock<TIn, TOut>(
        private val map: (TIn) -> TOut
) : ITarget<TIn>, ISource<TOut> {
    override val defaultSource = DefaultSource<TIn>()

    private val targets = mutableListOf<ITarget<TOut>>()

    private val sourceCore = SourceCore<TOut>()

    private val targetCore = TargetCore<TIn> {
        val newId = sourceCore.offer(map(it))
        synchronized(targets) {
            for (target in targets)
                target.offer(newId, this)
        }
    }

    override fun offer(eventId: Long, source: ISource<TIn>) =
            targetCore.offer(eventId, source)

    override fun consume(id: Long) = sourceCore.consume(id)

    override fun linkTo(target: ITarget<TOut>): Link<TOut> {
        synchronized(target) { targets.add(target) }
        return Link(this, target)
    }

    override fun unlink(target: ITarget<TOut>) {
        synchronized(target) { targets.remove(target) }
    }
}
