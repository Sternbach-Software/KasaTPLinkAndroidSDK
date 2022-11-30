package sternbach.software.kasatplinksdk.request

data class RpcRequestBody(
    val deviceId: String,
    val api: API,
    val state: Boolean? = null,
) {
    enum class API(val paramString: String) {
        GET_SYS_INFO("get_sysinfo"),
        SET_RELAY_STATE("set_relay_state")
    }
}
