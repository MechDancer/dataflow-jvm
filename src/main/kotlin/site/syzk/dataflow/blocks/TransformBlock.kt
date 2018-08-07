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
    private val sourceCore = SourceCore(this)
    private val targetCore = TargetCore<TIn> { event ->
        val out = map(event)
        val newId = sourceCore.offer(out)
        synchronized(sourceCore.targets) {
            sourceCore.targets
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

    override fun linkTo(target: ITarget<TOut>) = sourceCore.linkTo(target)

    override fun unlink(target: ITarget<TOut>) = sourceCore.unlink(target)

    override fun receive(): TOut {
        synchronized(receiveLock) {
            while (!receivable) receiveLock.wait()
            receivable = false
            @Suppress("UNCHECKED_CAST") return value as TOut
        }
    }
}
