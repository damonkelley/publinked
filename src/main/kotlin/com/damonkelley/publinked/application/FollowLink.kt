package com.damonkelley.publinked.application

import com.damonkelley.publinked.application.ports.incoming.FollowLinkUseCase
import com.damonkelley.publinked.application.ports.incoming.Href
import com.damonkelley.publinked.application.ports.outgoing.FindByNamePort
import com.damonkelley.publinked.application.ports.outgoing.SaveLinkPort

class FollowLink(val reader: FindByNamePort, val writer: SaveLinkPort) : FollowLinkUseCase {
    override fun follow(name: String): Href? {
        return reader.find(name)
            ?.let { it.log("followed") }
            ?.let { println(it); it }
            ?.let { writer.save(it) }
            ?.let { Href(value = it.href) }
    }
}
