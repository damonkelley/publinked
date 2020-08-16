package com.damonkelley.publinked.adapters.incoming.web

import com.damonkelley.publinked.application.ports.incoming.FollowLinkUseCase
import com.damonkelley.publinked.application.ports.incoming.Href
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class FollowLinkController(val followLinkUseCase: FollowLinkUseCase) {
    @GetMapping("/{name}")
    fun follow(@PathVariable name: String): ResponseEntity<Any> {
        return followLinkUseCase.follow(name).let(this::present)
    }

    private fun present(href: Href?): ResponseEntity<Any> {
        return href?.let {
            ResponseEntity<Any>(
                HttpHeaders().apply { location = URI(it.value) },
                HttpStatus.PERMANENT_REDIRECT
            )
        }
            ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }
}
