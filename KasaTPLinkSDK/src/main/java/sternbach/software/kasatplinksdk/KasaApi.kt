package sternbach.software.kasatplinksdk

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url
import sternbach.software.kasatplinksdk.request.DeviceInfoRequestBody
import sternbach.software.kasatplinksdk.request.RpcRequestBody
import sternbach.software.kasatplinksdk.request.TokenRequestBody
import sternbach.software.kasatplinksdk.response.DeviceResponse
import sternbach.software.kasatplinksdk.response.RpcResponse
import sternbach.software.kasatplinksdk.response.TokenResponse
import java.util.*

interface KasaApi {

    /**
     * Gets this account's auth token
     * */
    @POST("/")
    suspend fun getToken(@Body body: TokenRequestBody): Response<TokenResponse>

    @POST("/")
    suspend fun getDeviceInfo(
        @Query("token") token: String,
        @Body body: DeviceInfoRequestBody
    ): Response<DeviceResponse>

    /**
     * I have no idea what "RPC" stands for...
     * */
    @POST
    suspend fun rpc(
        @Url serverUrl: String,
        @Query("token") token: String,
        @Body body: RpcRequestBody
    ): Response<RpcResponse>
}