package app.joybox.domain.vendor

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


class DuplicatedEmailException : RuntimeException()

@Service
class VendorService(
    private val vendorRepository: VendorRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun signUp(command: SignUpCommand) {
        // signup 후 이메일 전송
        if (vendorRepository.existsByEmail(command.email)) {
            throw DuplicatedEmailException()
        }

        val password = passwordEncoder.encode(command.password)
        val vendor = Vendor(command.email, password, command.name)
        vendorRepository.save(vendor)
    }
}
