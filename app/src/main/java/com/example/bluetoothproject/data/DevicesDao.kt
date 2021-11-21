package com.example.bluetoothproject.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bluetoothproject.models.ConnectedDeviceModel
import com.example.bluetoothproject.models.FondDeviceModel
import retrofit2.Response
import retrofit2.http.Url

@Dao
interface DevicesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDevice(connectedDeviceModel: ConnectedDeviceModel)

    @Update
    suspend fun updateDevice(connectedDeviceModel: ConnectedDeviceModel)

//   HATA Veriyor  : Not sure how to convert a Cursor to this method's return type
//    @Query("SELECT * FROM bluetoothdevices_database ORDER BY id DESC")
//    fun getHistoryFromLocal(): LiveData<List<ConnectedDeviceModel>>


    //hata sonrası tablo sayısı artırıp kodu uzatamak zorunda kaldım
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFondDevice(fondDeviceModel: FondDeviceModel)

    @Query("SELECT * FROM fonddevice_database")
    fun getFondDeviceHistory(): List<FondDeviceModel>

//    @Query("SELECT * FROM bluetoothdevices_database WHERE address =:address")
//    fun searchDevice(address : String) : ConnectedDeviceModel




}