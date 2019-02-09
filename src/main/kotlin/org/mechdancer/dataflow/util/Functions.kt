package org.mechdancer.dataflow.util

import org.mechdancer.dataflow.core.intefaces.ILink
import org.mechdancer.dataflow.core.intefaces.ISource
import org.mechdancer.dataflow.core.intefaces.ITarget

/** 根据链接集找到所有纯源 */
fun Iterable<ILink<*>>.pureSources(): Collection<ISource<*>> {
    val sources = map { it.source as ISource<*> }.distinct()
    val targets = map { it.target as ITarget<*> }.toHashSet()
    return sources.filter { it !is ITarget<*> || it !in targets }
}

/** 根据链接集找到所有纯宿 */
fun Iterable<ILink<*>>.pureTargets(): Collection<ITarget<*>> {
    val targets = map { it.target as ITarget<*> }.distinct()
    val sources = map { it.source as ISource<*> }.toHashSet()
    return targets.filter { it !is ISource<*> || it !in sources }
}
