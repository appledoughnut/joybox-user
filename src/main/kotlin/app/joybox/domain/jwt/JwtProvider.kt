package app.joybox.domain.jwt

import app.joybox.domain.vendor.Vendor
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.security.KeyPair
import java.time.LocalDateTime
import java.util.*

@Component
class JwtProvider(
    private val keyPair: KeyPair
) {
    fun generateJwtToken(vendor: Vendor): String {
        val millis = LocalDateTime.now().plusDays(1).second*1000
        return Jwts.builder()
            .setExpiration(Date(millis.toLong()))
            .claim("vendorId", vendor.id)
            .signWith(SignatureAlgorithm.RS256, keyPair.private)
            .compact()
    }
}