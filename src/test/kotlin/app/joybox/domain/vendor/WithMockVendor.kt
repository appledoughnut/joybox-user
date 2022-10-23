package app.joybox.domain.vendor

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockVendorSecurityContextFactory::class)
annotation class WithMockVendor(
    val vendorId: Long
)