package com.damonkelley.publinked

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Immutable
import org.hibernate.annotations.Subselect
import org.hibernate.annotations.Synchronize
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.Date
import java.util.Optional
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
data class Link(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    val id: String? = null,

    val name: String = UUID.randomUUID().toString(),

    val href: String? = null,

    @OneToMany(cascade = [CascadeType.ALL])
    @Fetch(FetchMode.JOIN)
    val activities: MutableList<Activity> = mutableListOf(),

    @field:CreationTimestamp
    val createdDate: Date? = null
)

@Entity
data class Activity(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    val id: String? = null,
    val name: String,

    @field:CreationTimestamp
    val createdDate: Date? = null
)

@Entity
@Immutable
@Subselect(
    """
    (SELECT
         l.id as link_id, a.name as name, count(a.*) as count from link l
         JOIN link_activities la on l.id = la.link_id
         JOIN activity a on la.activities_id = a.id
         GROUP BY l.id, a.name)
"""
)
@Synchronize("Link", "Activity")
data class ActivitySummary(
    var linkId: String? = "",
    @Id
    var name: String? = "",
    var count: Long? = 0L
)

@Entity
@Immutable
@Table(name = "link")
@Synchronize("Link", "Activity")
data class SummarizedLink(
    val href: String,
    @Id
    val id: String,
    val name: String,
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "linkId")
    val summary: List<ActivitySummary>
)

interface LinkRepository : CrudRepository<Link, String> {
    fun findByName(name: String): Link?
}

interface SummarizedLinkRepository : CrudRepository<SummarizedLink, String> {
    @Query("FROM SummarizedLink link LEFT JOIN FETCH link.summary")
    override fun findById(id: String): Optional<SummarizedLink>

    @Query("FROM SummarizedLink link LEFT JOIN FETCH link.summary WHERE link.name = :name")
    fun findByName(name: String): SummarizedLink?
}
