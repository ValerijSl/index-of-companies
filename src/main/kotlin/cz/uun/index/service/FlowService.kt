package cz.uun.index.service

import cz.uun.index.model.Company
import cz.uun.index.repository.CompanyRepository
import cz.uun.index.response.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.OffsetTime


@Service
class FlowService(
    private val companyRepository: CompanyRepository,
    private val aresService: AresService,
) {
    private fun getInfo(ico: String?, name: String?, adresa: String?) = CompanyInfo(
        adresa = adresa ?: "",
        ico = ico ?: "",
        obchodniJmeno = name ?: ""
    )

    private fun getEntity(ico: String?, name: String?, adresa: String?) = Company(
        ico = ico ?: "",
        name = name ?: "",
        adresa = adresa ?: "",
        updated = OffsetTime.now(),
    )

    fun getByIco(ico: String, onlySaved: Boolean): CompanyInfo? {
        val responseList = companyRepository.findByIco(ico)
        var response: Company? = null
        if (responseList.isNotEmpty()) {
            response = responseList[0]
        } else {
            throw ResourceNotFoundException()
        }
        return if (onlySaved) {
            getInfo(response?.ico, response?.name, response?.adresa)
        } else {
            try {
                val res = aresService.getCompanyByIco(ico)
                companyRepository.save(getEntity(res?.ico, res?.obchodniJmeno, res?.adresa))
                return res
            } catch (e: Exception) {
                throw ResourceNotFoundException()
            }
        }

    }
}