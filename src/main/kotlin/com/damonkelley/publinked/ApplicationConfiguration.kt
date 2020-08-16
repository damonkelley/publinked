package com.damonkelley.publinked

import com.damonkelley.publinked.adapters.outgoing.persistence.LinkPersistenceAdapter
import com.damonkelley.publinked.application.FollowLink
import com.damonkelley.publinked.application.ports.incoming.FollowLinkUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {
    @Bean
    fun followLinkUseCase(linkPersistenceAdapter: LinkPersistenceAdapter): FollowLinkUseCase {
        return FollowLink(reader = linkPersistenceAdapter, writer = linkPersistenceAdapter)
    }
}
