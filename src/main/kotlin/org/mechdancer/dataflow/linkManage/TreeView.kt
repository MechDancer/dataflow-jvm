package org.mechdancer.dataflow.linkManage

import org.mechdancer.dataflow.core.IBlock
import org.mechdancer.dataflow.core.ISource
import org.mechdancer.dataflow.core.Link

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
 * @param builder 字符串构造器
 * @param already 已经在树中的节点（识别环）
 * @param indent 缩进长度和格式
 */
private fun IBlock.treeView(
		builder: StringBuilder,
		already: MutableList<IBlock>,
		indent: Long) {
	builder.append("$name[$uuid]")
	if (this in already) {
		builder.append("[Loop!]\n")
	} else {
		builder.append("\n")
		already.add(this)
		val branch = this as? ISource<*>
		if (branch != null) {
			val format = { block: Link<*>, last: Boolean ->
				val list = mutableListOf<Boolean>()
				var copy = indent
				while (copy > 1) {
					list.add(copy % 2 > 0)
					copy /= 2
				}
				list.reverse()
				list.forEach { builder.append(if (it) " │" else "  ") }
				if (!last) {
					builder.append(" ├─")
					block.target.treeView(
							builder,
							already,
							2 * indent + 1)
				} else {
					builder.append(" └─")
					block.target.treeView(
							builder,
							already,
							2 * indent + 0)
				}
			}
			val list = Link.find(branch)
			if (list.isNotEmpty()) {
				list.dropLast(1).forEach { format(it, false) }
				format(list.last(), true)
			}
		}
	}
}
