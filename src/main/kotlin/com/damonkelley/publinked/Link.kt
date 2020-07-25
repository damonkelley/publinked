package com.damonkelley.publinked

import org.hibernate.annotations.GenericGenerator
import org.springframework.data.repository.CrudRepository
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Link(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    val id: String? = null,
    val href: String? = null
)

interface LinkRepository : CrudRepository<Link, String>
