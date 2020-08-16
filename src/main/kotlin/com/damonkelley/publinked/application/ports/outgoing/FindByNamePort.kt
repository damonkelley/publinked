package com.damonkelley.publinked.application.ports.outgoing

import com.damonkelley.publinked.domain.Link

interface FindByNamePort {
    fun find(name: String): Link?
}
