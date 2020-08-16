package com.damonkelley.publinked

import com.damonkelley.publinked.adapters.outgoing.persistence.ActivityEntity
import com.damonkelley.publinked.application.FollowLink
import com.damonkelley.publinked.application.ports.outgoing.FindByNamePort
import com.damonkelley.publinked.application.ports.outgoing.SaveLinkPort
import com.damonkelley.publinked.domain.Activity
import com.damonkelley.publinked.domain.Link
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

fun containActivities(names: Collection<String>) = object : Matcher<Collection<Activity>> {
    override fun test(value: Collection<Activity>): MatcherResult {
        return MatcherResult(
            value.map(Activity::name).containsAll(names),
            "String $value should include $names",
            "String $value should not include $names"
        )
    }
}

class AnotherFollowLinkTest : DescribeSpec({
    context("when a link is followed") {
        it("will add a followed activity") {

            val adapter = TestAdapter(mutableListOf(Link(href = "http://example.com", name = "name", id = "id")))

            FollowLink(reader = adapter, writer = adapter)
                .follow(name = "name")

            val activities = adapter.find(name = "name")?.activities.orEmpty()
            println(activities)

            activities should containActivities(listOf("followed"))
        }

        it("will return the href") {

            val adapter = TestAdapter(mutableListOf(Link(href = "http://example.com", name = "name", id = "id")))

            val href = FollowLink(reader = adapter, writer = adapter)
                .follow(name = "name")

            href?.value shouldBe "http://example.com"
        }
    }
})

class TestAdapter(private val links: MutableList<Link>) : FindByNamePort, SaveLinkPort {
    override fun find(name: String): Link? {
        return links.find { it.name == name }
    }

    override fun save(link: Link): Link {
        links.apply {
            removeIf { it.id == link.id!! }
            add(link)
        }

        return link
    }
}

