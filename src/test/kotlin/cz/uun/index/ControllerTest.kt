package cz.uun.index

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@SpringBootTest
@AutoConfigureMockMvc
class ControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should return company`(){
        mockMvc.perform(
            get("/api/v1/company")
                .queryParam("ico", "19382111")
        ).andDo { result ->
            val content = result.response.contentAsString
            assert(content == "{\"ico\":\"19382111\",\"obchodniJmeno\":\"Alpha Codes s.r.o.\",\"adresa\":\"K. MÃ¼ndla 513, 25230 Å\u0098evnice\"}")
        }
    }
    @Test
    fun `should return no company`(){
        mockMvc.perform(
            get("/api/v1/company")
                .queryParam("ico", "11")
        ).andDo { result ->
            val content = result.response.contentAsString
            assert(content == "")
        }
    }

}
