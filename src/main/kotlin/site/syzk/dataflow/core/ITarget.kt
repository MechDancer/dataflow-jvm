package site.syzk.dataflow.core

interface ITarget<T> {
    /**
     * 下次可用时间
     * 反馈 Postpone 时可能修改
     */
    val avaliableTime: Long

    /**
     * 产生一个事件
     */
    fun post(value: T) = post(event(value))

    /**
     * 接收一个事件
     */
    fun post(event: Event<T>): Feedback
}
