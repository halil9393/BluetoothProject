package com.metinozcura.rickandmorty.data.db.converter

import androidx.room.TypeConverter
import com.example.bluetoothproject.models.Service
import org.json.JSONObject



class SourceTypeConverter {
    @TypeConverter
    fun fromService(service: List<Service>): String {
        return JSONObject().apply {
            put("uuid", service[0].uuid)
            put("chr1", service[0].characteristics.get(0))
            put("chr2", service[0].characteristics.get(1))
        }.toString()
    }

    @TypeConverter
    fun toService(source: String): List<Service> {
        val json = JSONObject(source)
        val list = listOf<String>(json.getString("chr1"), json.getString("chr2"))
        return listOf(Service( list,json.get("uuid").toString() ))
    }


}