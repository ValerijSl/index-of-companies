package cz.uun.index.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

@Entity
@Table(name = "company")
class Company(

    @Id
    var ico: String? = null,

    var name: String? = null,

    var adresa: String? = null,

    @CreationTimestamp
    var updated: OffsetDateTime? = null

)