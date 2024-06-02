package cz.uun.index.controller

import cz.uun.index.service.AresService
import cz.uun.index.service.CompanyInfo
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
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