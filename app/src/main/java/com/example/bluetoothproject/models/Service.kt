package com.example.bluetoothproject.models


import com.google.gson.annotations.SerializedName


data class Service(
    @SerializedName("characteristics")
    val characteristics: List<String>,
    @SerializedName("uuid")
    val uuid: String
)