package sternbach.software.kasatplinksdk.response

data class DeviceResponse(
    val error_code: Int,
    val result: DeviceResult
) {
    data class DeviceResult(
        val deviceList: List<Device>
    ) {
        data class Device(
            val deviceType: String,
            val role: Int,
            val fwVer: String,
            val appServerUrl: String,
            val deviceRegion: String,
            val deviceId: String,
            val deviceName: String,
            val deviceHwVer: String,
            val alias: String,
            val deviceMac: String,
            val oemId: String,
            val deviceModel: String,
            val hwId: String,
            val fwId: String,
            val isSameRegion: Boolean,
            val status: Int
        )
    }
}