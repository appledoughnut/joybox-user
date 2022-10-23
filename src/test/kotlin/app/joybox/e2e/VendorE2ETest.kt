package app.joybox.e2e

import app.joybox.api.request.LoginRequest
import app.joybox.api.request.SignupRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class VendorE2ETest {

    @Autowired
    private lateinit var template: TestRestTemplate

    @Test
    fun `Should return 200 and jwt token when login`() {
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

    @Test
    fun `Should return 401 when get me with unauthorized user`() {
        val response: ResponseEntity<Any> = template.getForEntity("/api/vendor/me", null, ResponseEntity::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `Should return 200 when get me with logged in user`() {
        val email = "valid@email.com"
        val password = "password"
        val name = "name"

        val signupRequest = SignupRequest(email, password, name)
        val loginRequest = LoginRequest(email, password)

        template.postForEntity("/api/vendor/signup", signupRequest, ResponseEntity::class.java)
        val loginResponse = template.postForEntity("/api/vendor/login", loginRequest, Unit::class.java)

        val cookie = loginResponse.headers["Set-Cookie"]!![0]
        val header = HttpHeaders()
        header.add("Cookie", cookie)

        val response = template.exchange("/api/vendor/me", HttpMethod.GET, HttpEntity<Any>(header), Unit::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
    }
}