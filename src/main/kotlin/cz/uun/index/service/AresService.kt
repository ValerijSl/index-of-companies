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
        .bodyToMono(CompanyInfo::class.java)
}

data class CompanyInfo(
    val ico: String,
    val obchodniJmeno: String,
    val sidlo: HashMap<String, String>
)