package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.TargetCore
import java.util.*

/**
 * 转换模块
 * @param map 转换函数
 */
class TransformBlock<TIn, TOut>(
    override val name: String = "transform",
    options: ExecutableOptions = ExecutableOptions(),
    private val map: (TIn) -> TOut
) : IPropagatorBlock<TIn, TOut>, IReceivable<TOut> {
    override val uuid = UUID.randomUUID()!!
    override val defaultSource by lazy { DefaultSource(this) }

    //--------------------------
    // ITarget & ISource
    //--------------------------

    private val sourceCore = SourceCore<TOut>(Int.MAX_VALUE)
    private val targetCore = TargetCore<TIn>(options)
    { event ->
        map(event).let { out ->
            sourceCore.offer(out).let { newId ->
                Link[this]
                    .filter { it.options.predicate(out) }
                    .forEach { it.target.offer(newId, it) }
            }
        }
        synchronized(receiveLock) { receiveLock.notifyAll() }
    }

    //--------------------------
    // IReceivable
    //--------------------------

    private val receiveLock = Object()

    //--------------------------
    // Methods
    //--------------------------

    override fun offer(id: Long, link: Link<TIn>) = targetCore.offer(id, link)
    override fun consume(id: Long) = sourceCore.consume(id)

    override fun receive(): TOut {
        while (true) {
            synchronized(receiveLock) {
                sourceCore.consume().let {
                    if (it.first)
                        @Suppress("UNCHECKED_CAST")
                        return it.second as TOut
                    else
                        receiveLock.wait()
                }
            }
        }
    }
}
