package org.mechdancer.dataflow.builder

import org.mechdancer.dataflow.core.LinkOptions

data class LinkModel(
        val source: String,
        val target: String,
        val options: LinkOptions<*>
)
