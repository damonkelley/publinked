package com.damonkelley.publinked

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class LinkController(val repository: LinkRepository) {
    @GetMapping("/{id}")
    fun follow(@PathVariable id: String): ResponseEntity<Any> {
        return repository.findByIdOrNull(id)?.let { link ->
            ResponseEntity<Any>(
                HttpHeaders().apply { location = URI(link.href) }, HttpStatus.TEMPORARY_REDIRECT
            )
        }
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PostMapping
    fun create(@RequestBody link: Link): ResponseEntity<Any> {
        return repository.save(link).let { ResponseEntity(it, HttpStatus.CREATED) }
    }
}
