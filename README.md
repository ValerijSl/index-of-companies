# Dokumentace pro aplikaci v Kotlinu: Vyhledávání v databázi podle názvu společnosti a IČO

## 1. Úvod
Tato aplikace umožňuje vyhledávání informací o společnostech v databázi na základě názvu společnosti, IČO (identifikační číslo organizace) a sídla. Aplikace je napsána v programovacích jazycích Java a Kotlin a používá Spring Boot pro konfiguraci a správu připojení k databázi a externímu API.

## 2. Požadavky
- JDK 8 nebo vyšší
- Spring Boot
- JDBC driver pro používanou databázi (např. PostgreSQL)
- Databáze obsahující tabulku s informacemi o společnostech

## 3. Konfigurace
### 3.1. Nastavení databáze
Před spuštěním aplikace je nutné nakonfigurovat připojení k databázi pomocí `application.yml` souboru:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres?currentSchema=uun_index_companies
    password: 123
    username: ondrejbruha
  application:
    name: index-of-companies
  api-url:
    get-by-ico-url: https://ares.gov.cz/ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty
```

### 3.2. Struktura tabulky
Aplikace předpokládá, že tabulka s informacemi o společnostech má následující strukturu:

```sql
CREATE TABLE companies (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    ico VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL
);
```

## 4. Implementace

### 4.1. Konfigurace URL
Třída `ApiUrlConfig` slouží ke konfiguraci URL pro API volání:

```kotlin
package cz.uun.index.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.api-url")
class ApiUrlConfig(
    val getByIcoUrl: String
)
```

### 4.2. Třída AresService
Třída `AresService` slouží k vyhledávání informací o společnosti pomocí externího ARES API.

#### Logika volání ARES API
ARES API je voláno pomocí HTTP GET požadavku na konkrétní endpoint, který obsahuje název společnosti jako parametr. Odpověď na požadavek je zpracována a následně transformována do formátu `CompanyInfo`, který obsahuje informace o společnosti.

#### Endpointy
- **GET /api/v1/company** - Endpoint pro získání informací o společnosti.
  
  **Parametry:**
  - `name` (string, povinný) - Název společnosti nebo klíčová slova pro vyhledávání.
  
  **Vstup:**
  - `name` - Název společnosti nebo klíčová slova pro vyhledávání.
  
  **Výstup:**
  - `CompanyInfo` - Objekt obsahující informace o společnosti, včetně IČO, obchodního jména a adresy.
  
#### Popisy funkcí
- **`getCompanyByName(name: String): CompanyInfo?`**
  
  Metoda pro získání informací o společnosti podle názvu.
  
  **Parametry:**
  - `name` (string) - Název společnosti nebo klíčová slova pro vyhledávání.
  
  **Návratová hodnota:**
  - `CompanyInfo` - Objekt obsahující informace o společnosti (IČO, obchodní jméno, adresa).
  - `null`, pokud se nepodařilo získat informace.

#### Obchodní logika
ARES API slouží jako zdroj informací o společnostech, které jsou poté využity ve vaší aplikaci. Vyhledávání informací o společnostech podle názvu umožňuje aplikaci získat relevantní data na základě klíčových slov, která mohou být dále zpracována nebo zobrazena uživateli aplikace. Tímto způsobem může aplikace poskytovat užitečné informace o společnostech, které mohou být důležité pro uživatele aplikace.

Třída `AresService` slouží k vyhledávání informací o společnosti pomocí externího API:

```kotlin
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

    /**
     * Metoda pro získání informací o společnosti podle IČO.
     *
     * @param ico IČO společnosti.
     * @return Informace o společnosti.
     */
    fun getCompanyByIco(ico: String): CompanyInfo? {
        val companyInfoResponse = webClient.get()
            .uri("${apiUrlConfig.getByIcoUrl}/${ico}")
            .retrieve()
            .bodyToMono(CompanyInfoResponse::class.java)
            .block()

        return companyInfoResponse?.let {
            CompanyInfo(it.ico, it.obchodniJmeno, it.sidlo["textovaAdresa"] ?: "")
        }
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



### 4.3. Hlavní třída aplikace
Třída `IndexOfCompaniesApplication` je hlavní třídou aplikace, která inicializuje Spring Boot aplikaci:

```kotlin
package cz.uun.index

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IndexOfCompaniesApplication

fun main(args: Array<String>) {
    runApplication<IndexOfCompaniesApplication>(*args)
}
```

### 4.4. Třída CompanySearchApp
Třída `CompanySearchApp` obsahuje metody pro vyhledávání společností v databázi a pomocí externího API:

```java
package cz.uun.index;

import cz.uun.index.service.AresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CompanySearchApp implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AresService aresService;

    @Override
    public void run(String... args) throws Exception {
        String companyName = "Example Company";
        String companyIco = "12345678";
        String companyAddress = "Example Address";

        searchCompany(companyName, companyIco, companyAddress);
        searchCompanyByIco(companyIco);
    }

    public void searchCompany(String name, String ico, String address) {
        String query = "SELECT * FROM companies WHERE name = ? AND ico = ? AND address = ?";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, name, ico, address);

        for (Map<String, Object> row : results) {
            int id = (int) row.get("id");
            String companyName = (String) row.get("name");
            String companyIco = (String) row.get("ico");
            String companyAddress = (String) row.get("address");

            System.out.println("ID: " + id);
            System.out.println("Name: " + companyName);
            System.out.println("ICO: " + companyIco);
            System.out.println("Address: " + companyAddress);
        }
    }

    public void searchCompanyByIco(String ico) {
        aresService.getCompanyByIco(ico)
            .subscribe(companyInfo -> {
                System.out.println("ICO: " + companyInfo.getIco());
                System.out.println("Name: " + companyInfo.getObchodniJmeno());
                System.out.println("Address: " + companyInfo.getSidlo());
            });
    }
}
```

### 4.5. Třída Controller
Třída `Controller` obsahuje metodu pro získání informací o společnosti pomocí HTTP GET požadavku:

```kotlin
package cz.uun.index.controller

import cz.uun.index.service.AresService
import cz.uun.index.service.CompanyInfo
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import reactor.core.publisher.Mono

@Controller
@RequestMapping("/api/v1/")
class Controller(private val aresService: AresService) {

    @GetMapping("company")
    @ResponseBody
    fun getCompany(
        @RequestParam(value = "ico", required = true) ico: String
    ): CompanyInfo? {
        val res = aresService.getCompanyByIco(ico)
        return res
    }
}

