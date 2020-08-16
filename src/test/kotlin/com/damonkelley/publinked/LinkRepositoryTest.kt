package com.damonkelley.publinked

import com.damonkelley.publinked.adapters.outgoing.persistence.*
import com.damonkelley.publinked.domain.Activity
import com.damonkelley.publinked.domain.Link
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.singleElement
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class LinkRepositoryTest(@Autowired val repository: LinkRepository) : StringSpec({
    "it will save and fetch the associated activities" {
        val link = repository.save(
            LinkEntity(href = "http://example.com").apply {
                activities += ActivityEntity(name = "followed")
            }
        )

        link.activities shouldHave singleElement { it.name == "followed" }
    }

    "it can find a link by name" {
        repository.save(
            LinkEntity(href = "http://example.com", name = "sample")
        )

        repository.findByName("sample") shouldNotBe null
    }
})

@SpringBootTest
class LinkPersistenceAdapterTest(@Autowired val adapter: LinkPersistenceAdapter) : StringSpec({
    "it save a Link" {

        val unsavedLink = Link(
            name = "test-link", href = "http://example.com",
            activities = listOf(Activity(name = "followed"), Activity(name = "viewed"))
        )
        val link = adapter.save(unsavedLink)

        link.id shouldNotBe null
        link.activities shouldHaveSize 2
    }

    "it can find a link" {
        val name = "some-other-link"
        val savedLink = adapter.save(Link(name = name, href = "http://example.com"))

        val foundLink = adapter.find(name)

        foundLink!! shouldBe savedLink
    }
})

@SpringBootTest
class SummarizedLinkRepositoryTest(
    @Autowired val repository: LinkRepository,
    @Autowired val summarizedLinkRepository: SummarizedLinkRepository
) : StringSpec({
    "it will count the number of followed activities" {
        val link = repository.save(
            LinkEntity(href = "http://example.com").apply {
                activities += ActivityEntity(name = "followed")
                activities += ActivityEntity(name = "audited")
            }
        )

        val summarizedLink = summarizedLinkRepository.findByIdOrNull(link.id!!)!!

        val summary = summarizedLink.summary.map { it.name to it.count }

        summary shouldContainAll listOf("followed" to 1L, "audited" to 1L)
    }

    "it can find a link by name" {
        repository.save(
            LinkEntity(href = "http://example.com", name = "another-sample")
        )

        summarizedLinkRepository.findByName("sample") shouldNotBe null
    }
})
