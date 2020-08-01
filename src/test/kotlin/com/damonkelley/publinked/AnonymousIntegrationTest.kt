package com.damonkelley.publinked

import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class AnonymousIntegrationTest(@Autowired val browser: MockMvc) : BehaviorSpec({
    @Serializable
    data class Link(val href: String, val id: String? = null)

    @OptIn(UnstableDefault::class)
    val parser = Json(JsonConfiguration(ignoreUnknownKeys = true))

    Given("An anonymous user") {
        When("the link does not exist") {
            Then("it will return not found") {
                browser.get("/does-not-exist").andExpect { status { isNotFound } }
            }
        }

        When("a link does exist") {
            val json = json { "href" to "http://example.com" }

            val result =
                browser
                    .post("/api/links") {
                        content = json
                        contentType = MediaType.APPLICATION_JSON
                    }
                    .andReturn()

            val newLink =
                result.response.contentAsString.let {
                    parser.parse(Link.serializer(), it)
                }

            Then("you can get information about it") {
                browser.get("/${newLink.id}").andExpect { status { is3xxRedirection } }
            }
            Then("it will have information about it") {
                browser.get("/api/links/${newLink.id}")
                    .andExpect { status { isOk } }
                    .andExpect { jsonPath("$.summary") { isArray } }
            }
        }
    }
})
