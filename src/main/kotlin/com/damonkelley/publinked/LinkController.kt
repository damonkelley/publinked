package com.damonkelley.publinked

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/links")
class LinkController(
    val repository: LinkRepository,
    val summarizedLinkRepository: SummarizedLinkRepository
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: String): ResponseEntity<Any> {
        return summarizedLinkRepository.findByIdOrNull(id)
            ?.let { ResponseEntity<Any>(it, HttpStatus.OK) }
            ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PostMapping
    fun create(@RequestBody link: Link): ResponseEntity<Any> {
        return repository.save(link)
            .let { summarizedLinkRepository.findByIdOrNull(link.id!!) }
            .let { ResponseEntity(it, HttpStatus.CREATED) }
    }
}
