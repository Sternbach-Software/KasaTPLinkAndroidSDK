package sternbach.software.kasatplinksdk

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sternbach.software.kasatplinksdk.KasaDeviceManager.KasaDevice
import sternbach.software.kasatplinksdk.request.DeviceInfoRequestBody
import sternbach.software.kasatplinksdk.request.RpcRequestBody
import sternbach.software.kasatplinksdk.request.TokenRequestBody
import sternbach.software.kasatplinksdk.response.DeviceResponse
import sternbach.software.kasatplinksdk.response.RpcResponse
import sternbach.software.kasatplinksdk.response.TokenResponse
import sternbach.software.kasatplinksdk.serialization.GsonTypeAdapters

/**
 * Class for creating instances of [KasaDevice]s. May be used for other management purposes in the
 * future, such as deleting devices, moving devices between rooms or Homes, etc.
 *
 * Use this class by creating an instance, calling [authenticate], and then using the instance to
 * create [KasaDevice]s by calling [withDevice]. For example,
 *
 * ```
 * val manager = KasaDeviceManager()
 * manager.authenticate(email, password)
 * val device1 = manager.withDevice("device1")!!
 * ```
 * */
class KasaDeviceManager {

    /**
     * API auth token for this account
     * */
    private lateinit var token: String

    /**
     * All smart devices associated with this account
     * */
    private lateinit var devices: List<DeviceResponse.DeviceResult.Device>

    /**
     * Retrofit API client for the Kasa TP-Link API
     * */
    private val kasaApi by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://wap.tplinkcloud.com/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(
                            object : TypeToken<DeviceInfoRequestBody>() {}.type,
                            GsonTypeAdapters.deviceInfoRequestBodySerializer
                        )
                        .registerTypeAdapter(
                            object : TypeToken<TokenRequestBody>() {}.type,
                            GsonTypeAdapters.tokenRequestBodySerializer
                        )
                        .registerTypeAdapter(
                            object : TypeToken<RpcRequestBody>() {}.type,
                            GsonTypeAdapters.rpcRequestBodySerializer
                        )
                        .create()
                )
            )
            .build()
            .create(KasaApi::class.java)
    }


    /**
     * Authenticates the user and associates this instance of [KasaDeviceManager] with an account.
     * It is necessary to call this function before any commands can be issued to the smart devices.
     * */
    suspend fun authenticate(
        email: String,
        password: String,
        onError: suspend (t: Throwable, tokenResponse: Response<TokenResponse>, deviceResponse: Response<DeviceResponse>?) -> Unit = {_,_,_->}
    ) {
        if (::token.isInitialized) return
        val body = TokenRequestBody(email, password)
        val tokenResponse = kasaApi.getToken(body)
        var deviceInfoResponse: Response<DeviceResponse>? = null
        try {
            token = tokenResponse.body()!!.result.token
            deviceInfoResponse = kasaApi.getDeviceInfo(token, DeviceInfoRequestBody())
            devices =
                deviceInfoResponse.body()!!.result.deviceList
        } catch (t: Throwable) {
            onError(t, tokenResponse, deviceInfoResponse)
        }
    }

    /**
     * Returns all [KasaDevice]s associated with this account.
     * [authenticate] must be called before this function can be called.
     * @return null if unauthenticated.
     * */
    fun getAllKasaDevices(): List<KasaDevice>? {
        return if (!::token.isInitialized) return null
        else devices.map { it.asKasaDevice() }
    }

    /**
     * Returns all [DeviceResponse.DeviceResult.Device]s associated with this account.
     * These objects expose the internal implementation details of the Kasa TP-Link API.
     * [authenticate] must be called before this function can be called.
     * @return null if unauthenticated.
     * */
    fun getAllDevices(): List<DeviceResponse.DeviceResult.Device>? {
        return if (!::token.isInitialized) return null
        else devices
    }

    /**
     * Returns a [KasaDevice] representing the device with the provided [alias].
     * [authenticate] must be called before this function can be called.
     * @param alias the user-assigned name of the smart device (e.g. "Kitchen Lamp")
     * @return null if either unauthenticated or no device exists with the provided alias.
     * */
    fun withDevice(alias: String): KasaDevice? {
        return if (!::token.isInitialized) null
        else {
            devices.find { it.alias == alias }?.asKasaDevice()
        }
    }

    /**
     * Converts API model ([DeviceResponse.DeviceResult.Device]) to domain model ([KasaDevice])
     * */
    private fun DeviceResponse.DeviceResult.Device.asKasaDevice() =
        KasaDevice(
            alias,
            deviceId,
            appServerUrl,
            token,
            this@KasaDeviceManager
        )

    /**
     * Sets the state of the relay (the component in the smart device that controls whether
     * electricity flows)
     * @param newState if true, once this [Response] returns, the device with [deviceId]
     * will be turned on. Otherwise, it will be turned off.
     * */
    private suspend fun setRelayState(
        appServerUrl: String,
        token: String,
        deviceId: String,
        newState: Boolean
    ): Response<RpcResponse> = kasaApi.rpc(
        appServerUrl,
        token,
        RpcRequestBody(
            deviceId,
            RpcRequestBody.API.SET_RELAY_STATE,
            newState
        )
    )


    /**
     * Returns whether the device with the specified [alias] is currently on.
     * @return null if unauthenticated or a device with that [alias] doesn't exist.
     * */
    private suspend fun isDeviceOn(
        alias: String
    ): Boolean? {
        if (!::token.isInitialized) return null
        else {
            val device = devices.find { it.alias == alias } ?: return null
            return kasaApi.rpc(
                device.appServerUrl,
                token,
                RpcRequestBody(device.deviceId, RpcRequestBody.API.GET_SYS_INFO)
            ).body()?.result?.responseData?.system?.get_sysinfo?.relayState == 1
        }
    }

    /**
     * The entry point for issuing commands to the smart device.
     * */
    data class KasaDevice internal constructor(
        val alias: String,
        private val deviceId: String,
        private val appServerUrl: String,
        private val token: String,
        private val manager: KasaDeviceManager
    ) {
        suspend fun getIsOn() = manager.isDeviceOn(alias)!!
        suspend fun setIsOn(isOn: Boolean) =
            manager.setRelayState(appServerUrl, token, deviceId, isOn)

        suspend fun turnOn() = setIsOn(true)
        suspend fun turnOff() = setIsOn(false)
        suspend fun toggle() = manager.setRelayState(appServerUrl, token, deviceId, !getIsOn())
    }
}
