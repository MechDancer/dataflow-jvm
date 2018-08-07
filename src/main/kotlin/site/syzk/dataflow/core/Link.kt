package site.syzk.dataflow.core

class Link<T>(
        private val source: ISource<T>,
        private val target: ITarget<T>
) {
    fun dispose() = source.unlink(target)
}
