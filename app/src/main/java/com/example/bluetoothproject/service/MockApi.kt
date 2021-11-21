package com.example.bluetoothproject.service

import com.example.bluetoothproject.models.ConnectedDeviceModel
import retrofit2.http.*

interface MockApi {

    companion object {
        const val BASE_URL = "https://8a56f495-e1ec-4849-941c-8aab59c4631f.mock.pstmn.io/"
    }

    @POST("device/")
    suspend fun postDevice(@Body connectedDeviceModel: ConnectedDeviceModel)

    @PUT("device/")
    suspend fun putDevice(@Body connectedDeviceModel: ConnectedDeviceModel)

}