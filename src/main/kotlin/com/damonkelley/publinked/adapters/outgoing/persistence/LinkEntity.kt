package com.damonkelley.publinked.adapters.outgoing.persistence

import com.damonkelley.publinked.application.ports.outgoing.FindByNamePort
import com.damonkelley.publinked.application.ports.outgoing.SaveLinkPort
import com.damonkelley.publinked.domain.Activity
import com.damonkelley.publinked.domain.Link
import org.hibernate.annotations.*
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import java.util.*
import javax.persistence.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Table

@Entity(name = "Link")
data class LinkEntity(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    val id: String? = null,

    val name: String = UUID.randomUUID().toString(),

    val href: String? = null,

    @OneToMany(cascade = [CascadeType.ALL])
    @Fetch(FetchMode.JOIN)
    val activities: MutableList<ActivityEntity> = mutableListOf(),

    @field:CreationTimestamp
    val createdDate: Date? = null
)

@Entity(name = "Activity")
data class ActivityEntity(
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
data class ActivitySummaryEntity(
    var linkId: String? = "",
    @Id
    var name: String? = "",
    var count: Long? = 0L
)

@Entity(name = "SummarizedLink")
@Immutable
@Table(name = "link")
@Synchronize("Link", "Activity")
data class SummarizedLinkEntity(
    val href: String,
    @Id
    val id: String,
    val name: String,
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "linkId")
    val summary: List<ActivitySummaryEntity>
)

interface LinkRepository : CrudRepository<LinkEntity, String> {
    fun findByName(name: String): LinkEntity?
}

interface SummarizedLinkRepository : CrudRepository<SummarizedLinkEntity, String> {
    @Query("FROM SummarizedLink link LEFT JOIN FETCH link.summary")
    override fun findById(id: String): Optional<SummarizedLinkEntity>

    @Query("FROM SummarizedLink link LEFT JOIN FETCH link.summary WHERE link.name = :name")
    fun findByName(name: String): SummarizedLinkEntity?
}

@Component
class LinkPersistenceAdapter(private val repository: LinkRepository) : FindByNamePort, SaveLinkPort {
    override fun find(name: String): Link? {
        return repository.findByName(name)?.toLink()
    }

    override fun save(link: Link): Link {
        return link.toEntity()
            .let { repository.save(it) }
            .toLink()
    }

    private fun LinkEntity.toLink(): Link {
        return Link(
            id = id,
            name = name,
            href = href!!,
            activities = activities.map { activity -> Activity(id = activity.id, name = activity.name) }
        )
    }

    private fun Link.toEntity(): LinkEntity {
        return LinkEntity(
            id = id,
            name = name,
            href = href,
            activities = activities.map { ActivityEntity(id = it.id, name = it.name) }.toMutableList()
        )
    }
}
