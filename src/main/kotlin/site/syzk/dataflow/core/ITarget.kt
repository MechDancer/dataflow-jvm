package site.syzk.dataflow.core

interface ITarget<T> {
    /**
     * 默认源
     * 储存来自外部的事件
     */
    val defaultSource: DefaultSource<T>

    /**
     * 通知目标节点，链接了该节点的某个源有事件到来
     * 由链接节点的源调用
     * @param id 源内部事件的唯一标识，供节点查找
     * @param link 事件到来的链接
     */
    fun offer(id: Long, link: Link<T>): Feedback
}

/**
 * 从外部直接发送事件到目标节点
 */
fun <T> ITarget<T>.post(event: T) = defaultSource.offer(event).let { offer(it.first, it.second) }
