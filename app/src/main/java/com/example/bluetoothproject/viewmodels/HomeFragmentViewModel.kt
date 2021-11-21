package com.example.bluetoothproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bluetoothproject.models.FondDeviceModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeFragmentViewModel : ViewModel() {

    sealed class UiState {

        object Empty : UiState()

        class Success(val fondDevices: List<FondDeviceModel>) : UiState()
        class Fail(val exeption: Exception) : UiState()
        class Loading(val showLoading: Boolean) : UiState()
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    private val _deviceListState = MutableStateFlow<UiState>(UiState.Empty)
    var deviceListState : StateFlow<UiState> = _deviceListState




}