package cz.uun.index

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IndexOfCompaniesApplication

fun main(args: Array<String>) {
    runApplication<IndexOfCompaniesApplication>(*args)
}
