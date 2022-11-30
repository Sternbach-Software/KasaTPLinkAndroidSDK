package sternbach.software.kasatplinksdk.serialization

import com.google.gson.JsonObject
import com.google.gson.JsonSerializer
import sternbach.software.kasatplinksdk.request.DeviceInfoRequestBody
import sternbach.software.kasatplinksdk.request.RpcRequestBody
import sternbach.software.kasatplinksdk.request.TokenRequestBody
import java.util.*

object GsonTypeAdapters {
    val tokenRequestBodySerializer = JsonSerializer<TokenRequestBody> { src, _, _ ->
        JsonObject().apply {
            addProperty("method", "login")
            add("params", JsonObject().apply {
                addProperty("appType", "Kasa_Android")
                addProperty("cloudPassword", src.password)
                addProperty("cloudUserName", src.email)

                // Generate a deterministic UUID just in case TP-Link expects it to be reused, e.g. if
                // it represents a distinct API key then using random UUIDs might cause TP-Link's future
                // hypothetical user-accessible API dashboard to be spammed with a million distinct keys
                addProperty(
                    "terminalUUID", UUID.nameUUIDFromBytes(src.email.toByteArray()).toString()
                )
            })
        }
    }
    val deviceInfoRequestBodySerializer = JsonSerializer<DeviceInfoRequestBody> { _, _, _ ->
        JsonObject().apply {
            addProperty("method", "getDeviceList")
        }
    }
    val rpcRequestBodySerializer = JsonSerializer<RpcRequestBody> { src, _, _ ->
        JsonObject().apply {
            addProperty("method", "passthrough")
            add(
                "params",
                JsonObject().apply {
                    addProperty("deviceId", src.deviceId)
                    add(
                        "requestData",
                        JsonObject().apply {
                            add(
                                "system",
                                JsonObject().apply {
                                    if (src.api === RpcRequestBody.API.GET_SYS_INFO) add(
                                        RpcRequestBody.API.GET_SYS_INFO.paramString, JsonObject()
                                    )
                                    else add(
                                        RpcRequestBody.API.SET_RELAY_STATE.paramString,
                                        JsonObject().apply {
                                            addProperty(
                                                "state", src.state
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    }
}