package cz.uun.index.model

import jakarta.persistence.*
import java.time.OffsetTime

@Entity
@Table(name = "company")
class Company(

    @Id
    var ico: String? = null,

    var name: String? = null,

    var adresa: String? = null,

    var updated: OffsetTime? = null

)