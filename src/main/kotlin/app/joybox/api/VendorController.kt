package app.joybox.api

import app.joybox.api.request.LoginRequest
import app.joybox.api.request.SignUpRequest
import app.joybox.domain.vendor.DuplicatedEmailException
import app.joybox.domain.vendor.VendorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class VendorController(

    private val vendorService: VendorService
) {
    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid request: SignUpRequest): ResponseEntity<Any> {
        val command = request.toCommand()

        return try {
            vendorService.signUp(command)
            ResponseEntity.ok().build()
        } catch (e: DuplicatedEmailException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest) {
        val command = request.toCommand()
        return vendorService.login(command)
    }
}