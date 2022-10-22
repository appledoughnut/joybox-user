package app.joybox.e2e

import app.joybox.api.request.LoginRequest
import app.joybox.api.request.SignupRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class VendorE2ETest {

    @Autowired
    private lateinit var template: TestRestTemplate

    @Test
    fun `Should return 201 and jwt token when login`() {
        val email = "valid@email.com"
        val password = "password"
        val name = "name"

        val signupRequest = SignupRequest(email, password, name)
        val loginRequest = LoginRequest(email, password)

        template.postForEntity("/api/vendor/signup", signupRequest, ResponseEntity::class.java)
        val response = template.postForEntity("/api/vendor/login", loginRequest, ResponseEntity::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.headers["Set-Cookie"])
        assertNotNull(response.headers["Set-Cookie"]!![0])
    }
}