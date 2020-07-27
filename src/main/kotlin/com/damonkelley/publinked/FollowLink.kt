package com.damonkelley.publinked

import org.springframework.data.repository.findByIdOrNull

class FollowLink(private val repository: LinkRepository) {
    fun <T> interact(id: String, present: (link: Link?) -> T): T {
        return repository
            .findByIdOrNull(id)
            .let(::followed)
            ?.let(repository::save)
            .let(present)
    }

    private fun followed(link: Link?): Link? {
        return link?.apply { activities.add(Activity(name = "followed")) }
    }
}
