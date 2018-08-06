package site.syzk.dataflow.core

interface ISource<T> {
    /**
     * 消费一个事件
     * 如果成功，事件从源的队列中移除
     * 由得到源通知的宿调用
     * @param id 事件的标识
     */
    fun consume(id: Long): Pair<Boolean, T?>

    fun linkTo(target: ITarget<T>)
}
