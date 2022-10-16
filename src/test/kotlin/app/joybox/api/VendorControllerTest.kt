package app.joybox.api

import app.joybox.domain.vendor.VendorService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.stream.Stream

@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class VendorControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    lateinit var vendorService: VendorService

    @ParameterizedTest
    @MethodSource("emailProvider")
    fun `Should return 400 when email is invalid`(email: String?, expectedStatusCode: HttpStatus) {
        val request = mapOf(
            "email" to email,
            "password" to "password",
            "name" to "name"
        )

        val mapper = jacksonObjectMapper()

        mvc.post("/api/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { HttpStatus.BAD_REQUEST }
        }
    }

    fun emailProvider(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(null, HttpStatus.BAD_REQUEST),
            Arguments.of("invalid_email", HttpStatus.BAD_REQUEST),
            Arguments.of("valid@email.com", HttpStatus.OK),
        )
    }

    @Test
    fun `Should return 400 when password is invalid`() {

    }

    @Test
    fun `Should return 400 when name is invalid`() {

    }
}