package app.joybox.api

import app.joybox.config.SecurityConfig
import app.joybox.domain.vendor.VendorService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.stream.Stream

@WebMvcTest
@Import(SecurityConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class VendorControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @TestConfiguration
    class Config {
        @Bean
        fun vendorService() = mockk<VendorService>()
    }

    @Autowired
    private lateinit var vendorService: VendorService

    @ParameterizedTest
    @MethodSource("emailProvider")
    fun `Should return 400 when sign up with invalid email`(email: String?, expectedStatusCode: HttpStatus) {
        val request = mapOf(
            "email" to email,
            "password" to "password",
            "name" to "name"
        )

        val mapper = jacksonObjectMapper()

        mvc.post("/api/vendor/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(expectedStatusCode.value()) }
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
    fun `Should return 400 when sign up with invalid password`() {

    }

    @Test
    fun `Should return 400 when sign up with invalid name`() {

    }

    @Test
    fun `Should return 400 when sign in with invalid email`() {
        val request = mapOf(
            "email" to "valid@email.com",
            "password" to "password",
            "name" to "name"
        )

        val mapper = jacksonObjectMapper()

        mvc.post("/api/vendor/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }

        mvc.post("/api/vendor/login") {
            contentType
        }
    }

    @Test
    fun `Should return 201 and JWT token when login`() {
        val mapper = jacksonObjectMapper()

        val signupRequest = mapOf(
            "email" to "valid@email.com",
            "password" to "password",
            "name" to "name"
        )

        val token = "JWT token"

        every { vendorService.signup(any()) } returns Unit
        every { vendorService.login(any()) } returns token

        mvc.post("/api/vendor/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(signupRequest)
        }

        val request = mapOf(
            "email" to "valid@email.com",
            "password" to "password"
        )

        mvc.post("/api/vendor/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { HttpStatus.OK }
            cookie { token }
        }
    }
}