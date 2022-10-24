package app.joybox.domain.vendor

import app.joybox.domain.jwt.JwtProvider
import mu.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


class DuplicatedEmailException : RuntimeException()
class VendorNotFoundException : RuntimeException()
class PasswordNotMatchedException : RuntimeException()

val logger = KotlinLogging.logger {  }

@Service
class VendorService(
    private val vendorRepository: VendorRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider
) {
    fun signup(command: SignupCommand) {
        // TODO: signup 후 이메일 전송
        if (vendorRepository.existsByEmail(command.email)) {
            throw DuplicatedEmailException()
        }
        val password = passwordEncoder.encode(command.password)
        val vendor = Vendor(command.email, password, command.name)
        val v = vendorRepository.save(vendor)
        logger.info { "Vendor created" }
        logger.debug { "with vendor id = ${v.id}" }
    }

    fun login(command: LoginCommand): String {
        val vendor = vendorRepository.findByEmail(command.email) ?: throw VendorNotFoundException()
        if (!passwordEncoder.matches(command.password, vendor.password)) {
            throw PasswordNotMatchedException()
        }
        val token = jwtProvider.generateJwtToken(vendor)
        logger.info { "JWT token published" }
        logger.debug { "with vendor id = ${vendor.id}, token = \"$token\"" }
        return token
    }

    fun getVendor(vendorId: Long): Vendor {
        return vendorRepository.findById(vendorId).orElseThrow(::VendorNotFoundException)
    }
}