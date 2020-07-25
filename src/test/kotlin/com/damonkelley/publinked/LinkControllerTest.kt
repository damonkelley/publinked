package com.damonkelley.publinked

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.json
import kotlinx.serialization.stringify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest
@AutoConfigureMockMvc
class LinkControllerTest(@Autowired val browser: MockMvc) {
  @Serializable data class Link(val href: String, val id: String? = null)

  @Test
  fun `when the link does not exist, it will return not found`() {
    browser.get("/does-not-exist").andExpect { status { isNotFound } }
  }

  @Test
  fun `when a link does exist, it will redirect to the href`() {
    val result =
        browser
            .post("/") {
              content = Json.stringify(Link(href = "https://example.com/"))
              contentType = MediaType.APPLICATION_JSON
            }
            .andReturn()

    val newLink =
        result.response.contentAsString.let {
          Json(JsonConfiguration.Stable).parse(Link.serializer(), it)
        }

    browser.get("/${newLink.id}").andExpect { status { is3xxRedirection } }
  }

  @Test
  fun `it can create a link`() {
    val link = Link(href = "https://example.com/cat-picture")

    browser
        .post("/") {
          content = Json.stringify(link)
          contentType = MediaType.APPLICATION_JSON
        }
        .andExpect { status { isCreated } }
        .andExpect {
          jsonPath("$.href") { value("https://example.com/cat-picture") }
          jsonPath("$.id") { isString }
        }
  }
}
