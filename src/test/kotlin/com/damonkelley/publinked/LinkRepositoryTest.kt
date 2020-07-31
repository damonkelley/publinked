package com.damonkelley.publinked

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.singleElement
import io.kotest.matchers.shouldHave
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class LinkRepositoryTest(
    @Autowired val repository: LinkRepository,
    @Autowired val summarizedLinkRepository: SummarizedLinkRepository
) : StringSpec({

    "it will save and fetch the associated activities" {
        val link = repository.save(
            Link(href = "http://example.com").apply {
                activities += Activity(name = "followed")
            }
        )

        link.activities shouldHave singleElement { it.name == "followed" }
    }

    "it will count the number of followed activities" {
        val link = repository.save(
            Link(href = "http://example.com").apply {
                activities += Activity(name = "followed")
                activities += Activity(name = "audited")
            }
        )

        val summarizedLink = summarizedLinkRepository.findByIdOrNull(link.id!!)!!

        val summary = summarizedLink.summary.map { it.name to it.count }

        summary shouldContainAll listOf("followed" to 1L, "audited" to 1L)
    }
})
