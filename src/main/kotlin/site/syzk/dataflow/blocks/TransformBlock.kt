package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.*

/**
 * 转换模块
 * @param map 转换函数
 */
class TransformBlock<TIn, TOut>(
        private val map: (TIn) -> TOut
) : ITarget<TIn>, ISource<TOut>, IReceivable<TOut> {
    override val defaultSource = DefaultSource<TIn>()

    //--------------------------
    // ITarget & ISource
    //--------------------------
    private val targets = mutableListOf<ITarget<TOut>>()
    private val sourceCore = SourceCore<TOut>()
    private val targetCore = TargetCore<TIn> { event ->
        val out = map(event)
        val newId = sourceCore.offer(out)
        synchronized(targets) {
            targets
                    .map { it.offer(newId, this) }
                    .any { it.positive }
                    .otherwise {
                        sourceCore.drop(newId)
                        synchronized(receiveLock) {
                            receivable = true
                            value = out
                            receiveLock.notifyAll()
                        }
                    }
        }
    }

    //--------------------------
    // IReceivable
    //--------------------------
    private val receiveLock = Object()
    private var receivable = false
    private var value: TOut? = null

    //--------------------------
    // Methods
    //--------------------------
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

    override fun receive(): TOut {
        synchronized(receiveLock) {
            if (!receivable) receiveLock.wait()
            receivable = false
            @Suppress("UNCHECKED_CAST")
            return value as TOut
        }
    }
}
