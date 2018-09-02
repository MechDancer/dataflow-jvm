package org.mechdancer.dataflow.linkManage

import org.mechdancer.dataflow.core.IBlock
import org.mechdancer.dataflow.core.ILink
import org.mechdancer.dataflow.core.ISource
import org.mechdancer.dataflow.core.ITarget

/** 查找一级前驱节点 */
fun ITarget<*>.prior() =
    ILink.all()
        .filter { it.target === this }
        .map { it.source as IBlock }

/** 查找一级后继节点 */
fun ISource<*>.next() =
    ILink.all()
        .filter { it.source === this }
        .map { it.target as IBlock }

/** 递归查找前驱节点 */
private infix fun ITarget<*>.recordPrior(list: MutableList<IBlock>) {
    if (this in list) return
    this.prior().forEach {
        if (it !in list) {
            list.add(it)
            (it as? ITarget<*>)?.recordPrior(list)
        }
    }
}

/** 递归查找后继节点 */
private infix fun ISource<*>.recordNext(list: MutableList<IBlock>) {
    if (this in list) return
    this.next().forEach {
        if (it !in list) {
            list.add(it)
            (it as? ISource<*>)?.recordNext(list)
        }
    }
}

/** 列出所有前驱 */
fun ITarget<*>.allPrior() =
    mutableListOf<IBlock>()
        .apply { this@allPrior recordPrior this@apply }
        .toList()

/** 列出所有后继 */
fun ISource<*>.allNext() =
    mutableListOf<IBlock>()
        .apply { this@allNext recordNext this@apply }
        .toList()

/** 根据链接集找到所有纯源 */
fun List<ILink<*>>.pureSources() =
    this.map { it.target as IBlock }
        .distinct()
        .let { targets ->
            this.map { it.source as IBlock }
                .toSet()
                .filter { it !in targets }
        }
