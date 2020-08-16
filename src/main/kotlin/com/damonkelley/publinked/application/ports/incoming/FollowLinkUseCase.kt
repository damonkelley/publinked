package com.damonkelley.publinked.application.ports.incoming

inline class Href(val value: String)

interface FollowLinkUseCase {
    fun follow(name: String): Href?
}
