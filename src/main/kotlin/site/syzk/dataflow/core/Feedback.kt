package site.syzk.dataflow.core

enum class Feedback(val positive: Boolean) {
    Accepted(true),
    Declined(false),
    Postponed(true),
    NotAvailable(false)
}
