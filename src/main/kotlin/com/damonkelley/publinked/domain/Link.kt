package com.damonkelley.publinked.domain

data class Activity(
    val id: String? = null,
    val name: String
)

data class Link(
    val id: String? = null,
    val name: String,
    val href: String,
    val activities: List<Activity> = listOf()
) {
    fun log(activityName: String): Link {
        return copy(activities = listOf(*activities.toTypedArray(), Activity(name = activityName)))
    }
}
