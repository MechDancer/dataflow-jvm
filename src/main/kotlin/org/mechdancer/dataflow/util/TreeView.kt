package org.mechdancer.dataflow.util

import org.mechdancer.dataflow.core.IBlock
import org.mechdancer.dataflow.core.ISource
import org.mechdancer.dataflow.core.ITarget
import org.mechdancer.dataflow.core.internal.view

/**
 * 按树状图显示从源出发的拓扑
 */
fun IBlock.treeView(): String {
    val builder = StringBuilder()
    treeView(builder, mutableListOf(), 1)
    return builder.toString()
}

/**
 * 递归构造树
 *
 * @param builder 字符串构造器
 * @param already 已经在树中的节点（识别环）
 * @param indent 缩进长度和格式
 */
private fun IBlock.treeView(
        builder: StringBuilder,
        already: MutableList<IBlock>,
        indent: Long
) {
    //显示自己
    builder.append(view())
    //判断环路
    if (this in already) {
        builder.append("[Loop!!!]\n")
        return
    }
    already.add(this)
    builder.append("\n")
    //判断子树
    val branch = (this as? ISource<*>)
            ?.targets
            ?.takeUnless(Set<*>::isEmpty)
            ?.toList()
            ?: return
    //画图函数
    val format = { block: ITarget<*>, last: Boolean ->
        //画缩进
        val list = mutableListOf<Boolean>()
        var copy = indent
        while (copy > 1) {
            list.add(copy % 2 > 0)
            copy /= 2
        }
        list.reverse()
        list.forEach { builder.append(if (it) " │ " else "   ") }
        //画子树
        if (!last) {
            builder.append(" ├─")
            block.treeView(
                    builder,
                    already,
                    2 * indent + 1
            )
        } else {
            builder.append(" └─")
            block.treeView(
                    builder,
                    already,
                    2 * indent + 0
            )
        }
    }
    //画图
    branch.dropLast(1).forEach { format(it, false) }
    format(branch.last(), true)
}
