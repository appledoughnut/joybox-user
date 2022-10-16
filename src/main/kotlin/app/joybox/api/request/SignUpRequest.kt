package app.joybox.api.request

import app.joybox.domain.vendor.SignUpCommand
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email

class SignUpRequest(
    @Email
    val email: String,
    @Length(min=8, max=16)
    val password: String,
    @Length(min=1, max=50)
    val name: String
) {
    fun toCommand(): SignUpCommand {
        return SignUpCommand(email, password, name)
    }
}
