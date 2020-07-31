package com.damonkelley.publinked

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.spring.SpringAutowireConstructorExtension
import io.kotest.spring.SpringListener

object ProjectConfig : AbstractProjectConfig() {
    override fun listeners() = listOf(SpringListener)
    override fun extensions() = listOf(SpringAutowireConstructorExtension)
}
