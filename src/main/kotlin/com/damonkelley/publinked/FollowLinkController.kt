package com.damonkelley.publinked

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class FollowLinkController(val repository: LinkRepository) {
    @GetMapping("/{id}")
    fun follow(@PathVariable id: String): ResponseEntity<Any> {
        return FollowLink(repository).interact(id, ::present)
    }

    private fun present(link: Link?): ResponseEntity<Any> {
        return if (link != null) {
            ResponseEntity(
                HttpHeaders().apply { location = URI(link.href) },
                HttpStatus.PERMANENT_REDIRECT
            )
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
