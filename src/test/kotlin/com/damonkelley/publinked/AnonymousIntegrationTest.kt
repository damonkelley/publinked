package com.damonkelley.publinked

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.spring.SpringListener
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@WithAnonymousUser
class AnonymousIntegrationTest(@Autowired val browser: MockMvc) : BehaviorSpec({
    listener(SpringListener)

    @Serializable
    data class Link(val href: String, val name: String? = null)

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
                    .andDo { print() }
                    .andReturn()

            val newLink =
                result.response.contentAsString.let {
                    println(it)
                    parser.parse(Link.serializer(), it)
                }

            Then("you can get information about it") {
                browser.get("/${newLink.name}").andExpect { status { is3xxRedirection } }
            }
            Then("it will have information about it") {
                browser.get("/api/links/${newLink.name}")
                    .andExpect { status { isOk } }
                    .andExpect { jsonPath("$.summary") { isArray } }
            }
        }
    }
})

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class AuthenticatedUserIntegrationTest(@Autowired val browser: MockMvc) : BehaviorSpec({
    listener(SpringListener)

    Given("An authenticated user") {
        Then("they can created a link with a custom id") {
            val json = json {
                "name" to "my-custom-id"
                "href" to "http://example.com"
            }

            browser
                .post("/api/links") {
                    content = json
                    contentType = MediaType.APPLICATION_JSON
                }
                .andDo { print() }
                .andReturn()

            browser.get("/my-custom-id")
                .andExpect { status { is3xxRedirection } }
        }
    }
})
