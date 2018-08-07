package site.syzk.dataflow.core

/**
 * 链接信息
 * @param source 事件源
 * @param target 事件宿
 */
class Link<T>(
        private val source: ISource<T>,
        private val target: ITarget<T>
) {
    fun dispose() = source.unlink(target)
}
