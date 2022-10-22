package app.joybox.domain.vendor

import app.joybox.domain.jwt.JwtProvider
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


class DuplicatedEmailException : RuntimeException()
class InvalidAuthenticationException : RuntimeException()

@Service
class VendorService(
    private val vendorRepository: VendorRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider
) : UserDetailsService {
    fun signup(command: SignupCommand) {
        // signup 후 이메일 전송
        if (vendorRepository.existsByEmail(command.email)) {
            throw DuplicatedEmailException()
        }

        val password = passwordEncoder.encode(command.password)
        val vendor = Vendor(command.email, password, command.name)
        vendorRepository.save(vendor)
    }

    fun login(command: LoginCommand): String {
        try {
            val vendor = vendorRepository.findByEmail(command.email) ?: throw RuntimeException() //TODO
            if (!passwordEncoder.matches(command.password, vendor.password)) {
                throw RuntimeException() // TODO
            }
            return jwtProvider.generateJwtToken(vendor)
        } catch (e: AuthenticationException) {
            throw InvalidAuthenticationException()
        }
    }

    override fun loadUserByUsername(email: String): UserDetails? {
        val vendor = vendorRepository.findByEmail(email) ?: return null
        return UserDetailsImpl(vendor)
    }
}
