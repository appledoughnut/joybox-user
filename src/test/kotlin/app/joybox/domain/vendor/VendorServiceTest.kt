package app.joybox.domain.vendor

import app.joybox.domain.jwt.JwtProvider
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class VendorServiceTest {

    @MockK(relaxed = true)
    lateinit var vendorRepository: VendorRepository

    @MockK
    lateinit var passwordEncoder: PasswordEncoder

    @MockK
    lateinit var jwtProvider: JwtProvider

    @InjectMockKs
    lateinit var vendorService: VendorService

    @Test
    fun `Should throw DuplicatedEmailException when sign up with existing email`() {
        val command = SignupCommand("email@email.com", "password", "name")

        every { vendorRepository.existsByEmail(command.email) } returns true

        assertThrows<DuplicatedEmailException> {
            vendorService.signup(command)
        }
    }

    @Test
    fun `Should successfully save vendor when signup`() {
        val command = SignupCommand("email@email.com", "password", "name")
        val encodedPassword = "encoded password"

        every { vendorRepository.existsByEmail(command.email) } returns false
        every { passwordEncoder.encode(command.password) } returns encodedPassword

        val slot = slot<Vendor>()
        every { vendorRepository.save(capture(slot)) } returns Vendor("","","")

        vendorService.signup(command)

        assertEquals(command.email, slot.captured.email)
        assertEquals(encodedPassword, slot.captured.password)
        assertEquals(command.name, slot.captured.name)

    }

    @Test
    fun `Should successfully return JWT token when login`() {
        val email = "valid@email.com"
        val password = "password"
        val encodedPassword = "encoded password"
        val name = "name"
        val token = "JWT token"

        val command = LoginCommand(email, password)

        val vendor = Vendor(email, encodedPassword, name)
        every { vendorRepository.findByEmail(command.email) } returns vendor
        every { passwordEncoder.matches(password, encodedPassword) } returns true
        every { jwtProvider.generateJwtToken(vendor)} returns token

        assertEquals(token, vendorService.login(command))
    }
}