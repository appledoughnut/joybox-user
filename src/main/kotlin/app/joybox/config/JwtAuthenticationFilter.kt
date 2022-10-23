package app.joybox.config

import app.joybox.domain.jwt.JwtProvider
import app.joybox.domain.vendor.VendorPrincipal
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class InvalidHeaderException : RuntimeException()

class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val cookies = request.cookies
        if (cookies.isNullOrEmpty()) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            return
        }

        val token = cookies.find { it.name == "jwt" }
        if (token == null) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            return
        }

        val vendorId = jwtProvider.parseVendorId(token.value)
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(VendorPrincipal(vendorId), null)

        filterChain.doFilter(request, response)
    }
}