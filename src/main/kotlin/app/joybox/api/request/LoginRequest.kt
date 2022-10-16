package app.joybox.api.request

import app.joybox.domain.vendor.LoginCommand

class LoginRequest(
    val email: String,
    val password: String
) {
    fun toCommand(): LoginCommand {
        return LoginCommand(email, password)
    }
}
