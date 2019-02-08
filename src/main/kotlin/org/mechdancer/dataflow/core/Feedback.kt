package org.mechdancer.dataflow.core

/**
 * 反馈
 *
 * 目的节点告知源节点自己对事件的态度
 * @param positive 积极性，是否会消费事件
 */
enum class Feedback(val positive: Boolean) {
    Accepted(true),
    Declined(false),
    Postponed(true),
    NotAvailable(false),
    DecliningPermanently(false);
}
