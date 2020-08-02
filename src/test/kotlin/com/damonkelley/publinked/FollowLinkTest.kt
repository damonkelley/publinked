package com.damonkelley.publinked

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import java.util.Optional

fun containActivities(names: Collection<String>) = object : Matcher<Collection<Activity>> {
    override fun test(value: Collection<Activity>): MatcherResult {
        return MatcherResult(
            value.map(Activity::name).containsAll(names),
            "String $value should include $names",
            "String $value should not include $names"
        )
    }
}

class FollowLinkTest : DescribeSpec({
    context("when a link is followed") {
        it("will add a followed activity") {
            val repository = FakeRepository(mutableListOf(Link(href = "http://example.com", name = "name", id = "id")))
            val result = FollowLink(repository).interact(name = "name") { it }!!

            result.activities should containActivities(listOf("saved", "followed"))
        }
    }
})

class FakeRepository(private val links: MutableList<Link>) : LinkRepository {
    override fun findByName(name: String): Link? {
        return links.find { it.name == name }
    }

    override fun <S : Link?> save(entity: S): S {
        if (entity == null)
            throw error("entity can't be null")

        links.apply {
            removeIf { it.id == entity.id!! }
            entity.activities.add(Activity(name = "saved"))
            add(entity)
        }

        return entity
    }

    override fun findAll(): MutableIterable<Link> {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: String) {
        TODO("Not yet implemented")
    }

    override fun deleteAll(entities: MutableIterable<Link>) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun <S : Link?> saveAll(entities: MutableIterable<S>): MutableIterable<S> {
        TODO("Not yet implemented")
    }

    override fun count(): Long {
        TODO("Not yet implemented")
    }

    override fun findAllById(ids: MutableIterable<String>): MutableIterable<Link> {
        TODO("Not yet implemented")
    }

    override fun existsById(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun findById(id: String): Optional<Link> {
        return links.find { it.id == id }.let { Optional.ofNullable(it) }
    }

    override fun delete(entity: Link) {
        TODO("Not yet implemented")
    }
}
