package com.example.bluetoothproject.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fonddevice_database")
data class FondDeviceModel(
    val deviceName: String?,

    @NonNull
    @PrimaryKey
    val deviceAddress: String

)