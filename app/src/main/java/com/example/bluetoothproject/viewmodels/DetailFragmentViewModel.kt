package com.example.bluetoothproject.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothproject.data.BluetoothHistoryDB2
import com.example.bluetoothproject.models.ConnectedDeviceModel
import com.example.bluetoothproject.models.FondDeviceModel
import com.example.bluetoothproject.service.MockApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DetailFragmentViewModel(private val mockApi: MockApi) : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is detail Fragment detail detail"
    }
    val text: LiveData<String> = _text


    fun sendDeviceToApi(connectedDeviceModel: ConnectedDeviceModel){
        viewModelScope.launch(Dispatchers.IO) {

            try {
                mockApi.putDevice(connectedDeviceModel)
            } catch (exception: Exception) {
                Log.e("tag_error","Error ! $exception")
            }


        }
    }

    fun updateDeviceToApi(connectedDeviceModel: ConnectedDeviceModel){
        viewModelScope.launch(Dispatchers.IO) {

            try {
                mockApi.postDevice(connectedDeviceModel)
            } catch (exception: Exception) {
                Log.e("tag_error","Error ! $exception")
            }


        }
    }

    fun saveLocalDataBase(connectedDeviceModel: ConnectedDeviceModel,bluetoothHistoryDB2: BluetoothHistoryDB2){

        viewModelScope.launch(Dispatchers.IO) {


            try {
                bluetoothHistoryDB2.devicesDao().saveDevice(connectedDeviceModel)

                val fondDevice = FondDeviceModel(connectedDeviceModel.name,connectedDeviceModel.address)
                bluetoothHistoryDB2.devicesDao().saveFondDevice(fondDevice)


            } catch (exception: Exception) {
                Log.e("tag_error","Error ! $exception")
            }


        }
    }


}