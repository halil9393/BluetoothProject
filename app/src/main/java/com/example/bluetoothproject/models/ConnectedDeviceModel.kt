package com.example.bluetoothproject.models


import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.metinozcura.rickandmorty.data.db.converter.SourceTypeConverter

@Entity(tableName = "bluetoothdevices_database")
data class ConnectedDeviceModel(


    @PrimaryKey(autoGenerate = true)
    val id:Int,

    @SerializedName("address")
    val address: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("services")
    val services: ArrayList<Service>
)