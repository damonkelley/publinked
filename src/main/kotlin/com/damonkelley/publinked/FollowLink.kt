package com.damonkelley.publinked

import org.springframework.data.repository.findByIdOrNull

class FollowLink<T>(private val repository: LinkRepository) {
    fun interact(id: String, present: (link: Link?) -> T): T {
        return repository.findByIdOrNull(id).let(present)
    }
}
