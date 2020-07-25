package com.damonkelley.publinked

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class PublinkedApplication

fun main(args: Array<String>) {
  runApplication<PublinkedApplication>(*args)
}
