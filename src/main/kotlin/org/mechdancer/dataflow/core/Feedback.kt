package org.mechdancer.dataflow.core

enum class Feedback(val positive: Boolean) {
    Accepted(true),
    Declined(false),
    Postponed(true),
    NotAvailable(false)
}
