package cz.uun.index.service

import cz.uun.index.config.ApiUrlConfig
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
@EnableConfigurationProperties(ApiUrlConfig::class)
class AresService(
    private val apiUrlConfig: ApiUrlConfig
) {
    private val webClient: WebClient = WebClient.create()

    fun getCompanyByIco(ico: String) = webClient.get()
        .uri("${apiUrlConfig.getByIcoUrl}/${ico}")
        .retrieve()
        .bodyToMono(CompanyInfoResponse::class.java)
        .mapNotNull {
            it.sidlo.get("textovaAdresa")?.let { it1 -> CompanyInfo(it.ico, it.obchodniJmeno, it1) }
        }
        .block()

    fun postCompanyByName(name: String, count: Int = 100): List<CompanyInfo> {
        val response = webClient.post()
                .uri(apiUrlConfig.findByNameUrl) // Ensure this is the correct URL for POST
                .body(BodyInserters.fromValue(mapOf(
                        "pocet" to count,
                        "obchodniJmeno" to name,
                        // Assuming you don't need the "sidlo" field as it's not provided in the use-case
                )))
                .retrieve()
                .bodyToMono(CompanyInfoResponseWrapper::class.java) // Assuming this class wraps the list of companies
                .block()  // This will wait for the data to be available and block the thread

        // Transform the fetched data into a List<CompanyInfo>
        return response?.ekonomickeSubjekty?.map { entity ->
            CompanyInfo(
                    ico = entity.ico,
                    obchodniJmeno = entity.obchodniJmeno,
                    adresa = entity.sidlo["textovaAdresa"] ?: "Not provided"
            )
        } ?: emptyList() // Return an empty list if null
    }

}

data class CompanyInfoResponse(
    val ico: String,
    val obchodniJmeno: String,
    val sidlo: HashMap<String, String>
)

data class CompanyInfo(
    val ico: String,
    val obchodniJmeno: String,
    val adresa: String
)

data class CompanyInfoResponseWrapper(
        val pocetCelkem: Int,
        val ekonomickeSubjekty: List<CompanyInfoResponse>
)
