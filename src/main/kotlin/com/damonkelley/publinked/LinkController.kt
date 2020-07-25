package com.damonkelley.publinked

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/links")
class LinkController(val repository: LinkRepository) {
    @GetMapping("/{id}")
    fun get(@RequestParam id: String): ResponseEntity<Any> {
        return repository.findByIdOrNull(id)?.let { ResponseEntity<Any>(it, HttpStatus.OK) }
            ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PostMapping
    fun create(@RequestBody link: Link): ResponseEntity<Any> {
        return repository.save(link).let {
            ResponseEntity(
                it,
                HttpStatus.CREATED
            )
        }
    }
}
