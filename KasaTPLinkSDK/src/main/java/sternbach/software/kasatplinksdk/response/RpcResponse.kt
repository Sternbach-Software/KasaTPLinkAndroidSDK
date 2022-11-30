package sternbach.software.kasatplinksdk.response

import com.google.gson.annotations.SerializedName

data class RpcResponse(
    val errorCode: Int,
    val result: RpcResponseResult
) {
    data class RpcResponseResult(
        val responseData: ResponseData
    ) {
        data class ResponseData(
            val system: System
        ) {
            data class System(
                val get_sysinfo: GetSysInfo? = null,
                val set_relay_state: SetRelayState? = null
            ) {
                data class SetRelayState(
                    val state: Boolean? = null,
                    val err_code: Int? = null
                )
                data class GetSysInfo(
                    @SerializedName("hw_ver") var hwVer: String? = null,
                    @SerializedName("err_code") var errCode: Int? = null,
                    @SerializedName("deviceId") var deviceId: String? = null,
                    @SerializedName("longitude_i") var longitudeI: Int? = null,
                    @SerializedName("mac") var mac: String? = null,
                    @SerializedName("icon_hash") var iconHash: String? = null,
                    @SerializedName("ntc_state") var ntcState: Int? = null,
                    @SerializedName("updating") var updating: Int? = null,
                    @SerializedName("led_off") var ledOff: Int? = null,
                    @SerializedName("feature") var feature: String? = null,
                    @SerializedName("on_time") var onTime: Int? = null,
                    @SerializedName("relay_state") var relayState: Int? = null,
                    @SerializedName("oemId") var oemId: String? = null,
                    @SerializedName("alias") var alias: String? = null,
                    @SerializedName("model") var model: String? = null,
                    @SerializedName("mic_type") var micType: String? = null,
                    @SerializedName("dev_name") var devName: String? = null,
                    @SerializedName("rssi") var rssi: Int? = null,
                    @SerializedName("latitude_i") var latitudeI: Int? = null,
                    @SerializedName("obd_src") var obdSrc: String? = null,
                    @SerializedName("active_mode") var activeMode: String? = null,
                    @SerializedName("next_action") var nextAction: NextAction? = NextAction(),
                    @SerializedName("hwId") var hwId: String? = null,
                    @SerializedName("sw_ver") var swVer: String? = null,
                    @SerializedName("status") var status: String? = null
                ) {

                    data class NextAction(

                        @SerializedName("type") var type: Int? = null

                    )
                }
            }
        }
    }
}