package com.damonkelley.publinked

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/links")
class LinkController(
    val repository: LinkRepository,
    val summarizedLinkRepository: SummarizedLinkRepository
) {
    @GetMapping("/{name}")
    fun get(@PathVariable name: String): ResponseEntity<Any> {
        return summarizedLinkRepository.findByName(name)
            ?.let { ResponseEntity<Any>(it, HttpStatus.OK) }
            ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PostMapping
    fun create(@RequestBody link: Link, authentication: Authentication?) =
        when (authentication) {
            null -> CreateAnonymousLink(repository, summarizedLinkRepository).interact(link) {
                ResponseEntity(it, HttpStatus.CREATED)
            }
            else -> CreateNamedLink(repository, summarizedLinkRepository).interact(link) {
                ResponseEntity(it, HttpStatus.CREATED)
            }
        }
}

class CreateAnonymousLink(
    private val writer: LinkRepository,
    private val reader: SummarizedLinkRepository
) {
    fun <T> interact(link: Link, present: (SummarizedLink?) -> T): T {
        return link.copy(name = UUID.randomUUID().toString())
            .let { writer.save(it) }
            .let { reader.findByName(it.name) }
            .let { present(it) }
    }
}

class CreateNamedLink(
    private val writer: LinkRepository,
    private val reader: SummarizedLinkRepository
) {
    fun <T> interact(link: Link, present: (SummarizedLink?) -> T): T {
        return writer.save(link)
            .let { reader.findByName(it.name) }
            .let { present(it) }
    }
}
