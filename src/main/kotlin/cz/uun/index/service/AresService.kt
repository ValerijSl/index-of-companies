package cz.uun.index.service

import cz.uun.index.config.ApiUrlConfig
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

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