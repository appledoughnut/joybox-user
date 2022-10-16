package app.joybox.domain.vendor

import org.springframework.stereotype.Service

@Service
class VendorService(
    private val vendorRepository: VendorRepository
) {
    fun signUp(signUpCommand: SignUpCommand) {
        // validation check
        // 같은 email or name 이 있는지
        // password encryption
        // signup 후 이메일 전송

    }
}
