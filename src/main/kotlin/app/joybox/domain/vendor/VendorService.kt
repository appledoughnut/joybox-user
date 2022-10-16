package app.joybox.domain.vendor

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


class DuplicatedEmailException : RuntimeException()

@Service
class VendorService(
    private val vendorRepository: VendorRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager
) : UserDetailsService {
    fun signUp(command: SignUpCommand) {
        // signup 후 이메일 전송
        if (vendorRepository.existsByEmail(command.email)) {
            throw DuplicatedEmailException()
        }

        val password = passwordEncoder.encode(command.password)
        val vendor = Vendor(command.email, password, command.name)
        vendorRepository.save(vendor)
    }

    fun login(command: LoginCommand) {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(command.email, command.password)
        )

        SecurityContextHolder.getContext().authentication = authentication
    }

    override fun loadUserByUsername(email: String): UserDetails? {
        val vendor = vendorRepository.findByEmail(email) ?: return null
        return UserDetailsImpl(vendor)
    }
}
