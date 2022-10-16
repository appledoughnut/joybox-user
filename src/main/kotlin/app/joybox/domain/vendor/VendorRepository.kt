package app.joybox.domain.vendor

import org.springframework.data.jpa.repository.JpaRepository

interface VendorRepository: JpaRepository<Vendor, Long> {
    fun findByEmail(email: String): Vendor?

    fun existsByEmail(email: String): Boolean
}
