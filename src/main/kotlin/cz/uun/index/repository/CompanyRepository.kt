package cz.uun.index.repository

import cz.uun.index.model.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository: JpaRepository<Company, String> {
    @Query("""select company from Company company""")
    fun findByIco(ico: String): List<Company>
}

interface DBResponse {
    val ico: String?
    val name: String?
    val adresa: String?
    val updated: String?
}