package com.damonkelley.publinked.application.ports.outgoing

import com.damonkelley.publinked.domain.Link

interface SaveLinkPort {
    fun save(link: Link): Link
}
