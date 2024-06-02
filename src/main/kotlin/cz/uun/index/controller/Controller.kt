package cz.uun.index.controller

import cz.uun.index.service.AresService
import cz.uun.index.service.CompanyInfo
import cz.uun.index.service.FlowService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/api/v1/")
class Controller(
    private val aresService: AresService,
    private val flowService: FlowService
) {
    @GetMapping("company")
    @ResponseBody
    fun getCompany(
        @RequestParam(value = "ico", required = true) ico: String,
    ) = flowService.getByIco(ico)
}