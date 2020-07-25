package com.damonkelley.publinked

import java.net.URI
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.web.header.Header
import org.springframework.web.bind.annotation.*

@RestController
class LinkController(val repository: LinkRepository) {
  @GetMapping("/{id}")
  fun follow(@PathVariable id: String): ResponseEntity<Any> {
    return repository.findByIdOrNull(id)?.let { link ->
      ResponseEntity<Any>(
          HttpHeaders().apply { location = URI(link.href) }, HttpStatus.TEMPORARY_REDIRECT)
    }
        ?: return ResponseEntity(HttpStatus.NOT_FOUND)
  }

  @PostMapping
  fun create(@RequestBody link: Link): ResponseEntity<Any> {
    return repository.save(link).let { ResponseEntity(it, HttpStatus.CREATED) }
  }
}
