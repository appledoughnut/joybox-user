package app.joybox.api

import app.joybox.api.request.LoginRequest
import app.joybox.api.request.SignupRequest
import app.joybox.domain.vendor.DuplicatedEmailException
import app.joybox.domain.vendor.InvalidAuthenticationException
import app.joybox.domain.vendor.VendorPrincipal
import app.joybox.domain.vendor.VendorService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/api/vendor")
class VendorController(

    private val vendorService: VendorService
) {
    @PostMapping("/signup")
    fun signup(@RequestBody @Valid request: SignupRequest): ResponseEntity<Any> {
        return try {
            vendorService.signup(request.toCommand())
            ResponseEntity.ok().build()
        } catch (e: DuplicatedEmailException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<Any> {
        val command = request.toCommand()
        return try {
            val token = vendorService.login(command)

            val cookie = Cookie("jwt", token)

            cookie.maxAge = 24 * 60 * 60

            cookie.secure = true
            cookie.isHttpOnly = true
//            cookie.path = "/"

            response.addCookie(cookie)

            ResponseEntity.ok().build()
        } catch (e: InvalidAuthenticationException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal principal: VendorPrincipal): ResponseEntity<Any>{
        return ResponseEntity.ok().build() // TODO
    }
}