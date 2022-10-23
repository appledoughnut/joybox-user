package app.joybox.domain.jwt

import app.joybox.domain.vendor.Vendor
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.security.KeyPair
import java.util.*

class InvalidJwtTokenException : RuntimeException()

class JwtProvider(
    private val keyPair: KeyPair
) {
    fun generateJwtToken(vendor: Vendor): String {
        val millis = System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000

        return Jwts.builder()
            .setExpiration(Date(millis))
            .claim("vendorId", vendor.id.toString())
            .signWith(SignatureAlgorithm.RS256, keyPair.private)
            .compact()
    }

    fun parseVendorId(token: String): Long {
        return try {
            val claimsJwt = Jwts.parser()
                .setSigningKey(keyPair.public)
                .parseClaimsJws(token)

            val vendorIdString: String = claimsJwt.body["vendorId"] as String

            vendorIdString.toLong()

        } catch (e: Exception) {
            throw InvalidJwtTokenException()
        }
    }
}