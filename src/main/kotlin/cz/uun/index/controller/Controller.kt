package cz.uun.index.controller

import cz.uun.index.model.Company
import cz.uun.index.service.AresService
import cz.uun.index.service.CompanyInfo
import cz.uun.index.service.FlowService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

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

    @PostMapping("/search")
    fun search(@RequestBody request: SearchRequest): List<CompanyInfo> {
        return flowService.findByName(request.name);
    }
}

data class SearchRequest(val name: String)