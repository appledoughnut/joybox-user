package app.joybox.config

import app.joybox.domain.jwt.JwtProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.security.KeyFactory
import java.security.KeyPair
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*


val PUBLIC_ENDPOINTS = arrayOf("/api/vendor/signup", "/api/vendor/login", "/health", "/openapi.yaml")

@Configuration
class SecurityConfig {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun jwtProvider(keyPair: KeyPair) = JwtProvider(keyPair)

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { it.ignoring().antMatchers(*PUBLIC_ENDPOINTS) }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtProvider: JwtProvider): SecurityFilterChain {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(*PUBLIC_ENDPOINTS).permitAll()

            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .addFilterBefore(JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun jwtKeyPair(
        @Value("\${jwt.key.public}")
        publicKeyString: String,
        @Value("\${jwt.key.private}")
        privateKeyString: String
    ): KeyPair {
        val keyFactory = KeyFactory.getInstance("RSA")

        val publicBytes = Base64.getDecoder().decode(publicKeyString)
        val publicKeySpec = X509EncodedKeySpec(publicBytes)
        val publicKey = keyFactory.generatePublic(publicKeySpec)

        val privateBytes = Base64.getDecoder().decode(privateKeyString)
        val privateKeySpec = PKCS8EncodedKeySpec(privateBytes)
        val privateKey = keyFactory.generatePrivate(privateKeySpec)

        return KeyPair(publicKey, privateKey)
    }
}