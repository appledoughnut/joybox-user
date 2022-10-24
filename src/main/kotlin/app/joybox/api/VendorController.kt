package app.joybox.api

import app.joybox.api.request.LoginRequest
import app.joybox.api.request.SignupRequest
import app.joybox.api.response.MeResponse
import app.joybox.domain.vendor.*
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/vendor")
class VendorController(

    private val vendorService: VendorService
) {

    @PostMapping("/signup")
    fun signup(@RequestBody @Valid request: SignupRequest): ResponseEntity<Any> {
        logger.info { "Signup request" }
        return try {
            vendorService.signup(request.toCommand())
            ResponseEntity.ok().build()
        } catch (e: DuplicatedEmailException) {
            logger.debug { "$e" }
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<Any> {
        logger.info { "Login request" }
        val command = request.toCommand()
        return try {
            val token = vendorService.login(command)

            val cookie = Cookie("jwt", token)
            cookie.maxAge = 24 * 60 * 60
//            cookie.secure = true
            cookie.isHttpOnly = true
//            cookie.path = "/"
            response.addCookie(cookie)

            ResponseEntity.ok().build()
        } catch (e: VendorNotFoundException) {
            logger.debug { "$e" }
            ResponseEntity.badRequest().build()
        } catch (e: PasswordNotMatchedException) {
            logger.debug { "$e" }
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal principal: VendorPrincipal): ResponseEntity<MeResponse> {
        logger.info { "Get me request" }
        return try {
            val vendor = vendorService.getVendor(principal.vendorId)
            val response = MeResponse.from(vendor)
            ResponseEntity.ok(response)
        } catch (e: VendorNotFoundException) {
            logger.error { " $e: JWT token based authentication must not throw this exception." }
            ResponseEntity.internalServerError().build()
        }
    }
}