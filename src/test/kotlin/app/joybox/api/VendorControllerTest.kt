package app.joybox.api

import app.joybox.config.SecurityConfig
import app.joybox.domain.jwt.JwtProvider
import app.joybox.domain.vendor.*
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
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.stream.Stream
import javax.servlet.http.Cookie

@WebMvcTest
@Import(SecurityConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class VendorControllerTest {

    @Autowired
    lateinit var mvc: MockMvc


    @TestConfiguration
    class Config {
        @Bean
        fun jwtProvider() = mockk<JwtProvider>()

        @Bean
        fun vendorService(jwtProvider: JwtProvider) = mockk<VendorService>()
    }

//    @BeforeAll
//    fun beforeAll() {
//    }

    @Autowired
    private lateinit var vendorService: VendorService

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @ParameterizedTest
    @MethodSource("emailProvider")
    fun `Should return 400 with invalid email and 200 with valid email when signup`(email: String?, expectedStatusCode: HttpStatus) {
        val request = mapOf(
            "email" to email,
            "password" to "password",
            "name" to "name"
        )

        val mapper = jacksonObjectMapper()

        every { vendorService.signup(any()) } returns Unit

        mvc.post("/api/vendor/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(expectedStatusCode.value()) }
        }
    }

    private fun emailProvider(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(null, HttpStatus.BAD_REQUEST),
            Arguments.of("invalid_email", HttpStatus.BAD_REQUEST),
            Arguments.of("valid@email.com", HttpStatus.OK)
        )
    }

    @ParameterizedTest
    @MethodSource("passwordProvider")
    fun `Should return 400 with invalid password and 200 with valid password when signup`(password: String?, expectedStatusCode: HttpStatus) {
        val request = mapOf(
            "email" to "valid@email.com",
            "password" to password,
            "name" to "name"
        )

        every { vendorService.signup(any()) } returns Unit

        val mapper = jacksonObjectMapper()

        mvc.post("/api/vendor/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(expectedStatusCode.value()) }
        }
    }

    @ParameterizedTest
    @MethodSource("nameProvider")
    fun `Should return 400 with invalid name and 200 with valid name when sign up`(name: String?, expectedStatusCode: HttpStatus) {
        val request = mapOf(
            "email" to "valid@email.com",
            "password" to "password",
            "name" to name
        )

        every { vendorService.signup(any()) } returns Unit

        val mapper = jacksonObjectMapper()

        mvc.post("/api/vendor/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(expectedStatusCode.value()) }
        }
    }

    @Test
    fun `Should return 400 when signup with existing email`() {
        val request = mapOf(
            "email" to "existing@email.com",
            "password" to "password",
            "name" to "name"
        )

        val mapper = jacksonObjectMapper()

        every { vendorService.signup(any()) } throws DuplicatedEmailException()

        mvc.post("/api/vendor/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `Should return 400 when sign in with invalid email`() {
        val request = mapOf(
            "email" to "invalid@email.com",
            "password" to "password"
        )

        val mapper = jacksonObjectMapper()

        every { vendorService.login(any()) } throws VendorNotFoundException()

        mvc.post("/api/vendor/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `Should return 400 when sign in with invalid password`() {
        val request = mapOf(
            "email" to "invalid@email.com",
            "password" to "password"
        )

        val mapper = jacksonObjectMapper()

        every { vendorService.login(any()) } throws PasswordNotMatchedException()

        mvc.post("/api/vendor/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `Should return 200 and JWT token when login`() {
        val mapper = jacksonObjectMapper()
        val token = "JWT token"
        val request = mapOf(
            "email" to "valid@email.com",
            "password" to "password"
        )

        every { vendorService.login(any()) } returns token

        mvc.post("/api/vendor/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            cookie { value("jwt", token) }
        }
    }

    @Test
    @WithMockVendor(vendorId = 1L)
    fun `Should return 200 and vendor info when get me`() {
        val email = "valid@email.com"
        val password = "password"
        val name = "name"
        val vendor = Vendor(email, password, name)
        val token = "JWT token"

        val vendorId = 1L

        every { jwtProvider.parseVendorId(token) } returns vendorId
        every { vendorService.getVendor(vendorId) } returns vendor

        val cookie = Cookie("jwt", token)

        cookie.maxAge = 24 * 60 * 60

        cookie.secure = true
        cookie.isHttpOnly = true

        mvc.get("/api/vendor/me") {
            cookie(cookie)
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.name") { value(vendor.name) }
                jsonPath("$.logoUrl") { value(vendor.logoUrl) }
            }
            .andDo { print() }
    }

    fun passwordProvider(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(null, HttpStatus.BAD_REQUEST),
            Arguments.of("1", HttpStatus.BAD_REQUEST),
            Arguments.of("123", HttpStatus.BAD_REQUEST),
            Arguments.of("123123123123123123123123", HttpStatus.BAD_REQUEST),
            Arguments.of("valid_password", HttpStatus.OK)

        )
    }

    private fun nameProvider(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(null, HttpStatus.BAD_REQUEST),
            Arguments.of("this name is toooooooooooooooooooooooooooooooooo long to create vendor...", HttpStatus.BAD_REQUEST),
            Arguments.of("n", HttpStatus.OK),
            Arguments.of("name", HttpStatus.OK)
        )
    }
}