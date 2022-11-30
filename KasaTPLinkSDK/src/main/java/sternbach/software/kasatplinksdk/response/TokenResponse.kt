package sternbach.software.kasatplinksdk.response

data class TokenResponse(
    val error_code: Int,
    val result: TokenResult
) {
    data class TokenResult(
        val accountId: Int,
        val regTime: String,
        val countryCode: String,
        val riskDetected: Int,
        val email: String,
        val token: String
    )
}