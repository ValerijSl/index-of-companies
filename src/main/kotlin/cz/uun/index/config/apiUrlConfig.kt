package cz.uun.index.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.api-url")
class ApiUrlConfig(
    val getByIcoUrl: String,
    val findByNameUrl: String
)