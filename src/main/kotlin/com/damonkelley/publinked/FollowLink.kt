package com.damonkelley.publinked

class FollowLink(private val repository: LinkRepository) {
    fun <T> interact(name: String, present: (link: Link?) -> T): T {
        return repository
            .findByName(name)
            .let(::followed)
            ?.let(repository::save)
            .let(present)
    }

    private fun followed(link: Link?): Link? {
        return link?.apply { activities.add(Activity(name = "followed")) }
    }
}
