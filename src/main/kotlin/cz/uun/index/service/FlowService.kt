package cz.uun.index.service

import cz.uun.index.model.Company
import cz.uun.index.repository.CompanyRepository
import cz.uun.index.response.ResourceNotFoundException
import org.springframework.stereotype.Service
import java.time.OffsetDateTime


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
        updated = OffsetDateTime.now(),
    )

    private fun handleDBResponse(responseList: List<Company>): Company {
        if (responseList.isNotEmpty()) {
            return responseList[0]
        } else {
            throw ResourceNotFoundException()
        }
    }

    private fun getFromAres(ico: String): CompanyInfo {
        val res = aresService.getCompanyByIco(ico)
        companyRepository.save(getEntity(res?.ico, res?.obchodniJmeno, res?.adresa))
        return getInfo(res?.ico, res?.obchodniJmeno, res?.adresa)
    }
    fun isValidIco(ico: String): Boolean {
        if (ico.length < 2 || ico.length > 15) {
            return false
        }
        if (!ico.all { it.isDigit() }) {
            return false
        }

        return true
    }
    fun getByIco(ico: String): CompanyInfo? {
        if (!isValidIco(ico)) return null
        val responseList = companyRepository.findByIco(ico)
        try {
            if (responseList.isNotEmpty()) {
                if (responseList[0].updated!! < OffsetDateTime.now().minusHours(1)) {
                    return getFromAres(ico)
                }
                return getInfo(responseList[0].ico, responseList[0].name, responseList[0].adresa)
            } else {
                return getFromAres(ico)
            }
        } catch (e: Exception) {
            throw ResourceNotFoundException()
        }
    }
    fun findByName(name: String): List<CompanyInfo>  {
        return aresService.postCompanyByName( "Divadlo");
    }
}