package app.joybox.api.response

import app.joybox.domain.vendor.Vendor

class MeResponse(
    val name: String,
    val logoUrl: String?
) {
    companion object {
        fun from(vendor: Vendor): MeResponse {
            return MeResponse(vendor.name, vendor.logoUrl)
        }
    }
}