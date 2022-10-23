package app.joybox.domain.vendor

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithMockVendorSecurityContextFactory: WithSecurityContextFactory<WithMockVendor> {
    override fun createSecurityContext(mockVendor: WithMockVendor): SecurityContext {
        val context = SecurityContextHolder.getContext()

        val vendorPrincipal = VendorPrincipal(mockVendor.vendorId)
        val authenticationToken = UsernamePasswordAuthenticationToken(vendorPrincipal, null)
        context.authentication = authenticationToken

        return context
    }

}
