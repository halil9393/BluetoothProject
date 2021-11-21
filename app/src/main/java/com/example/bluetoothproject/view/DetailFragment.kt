package com.example.bluetoothproject.view

import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.bluetoothproject.R
import com.example.bluetoothproject.data.BluetoothHistoryDB2
import com.example.bluetoothproject.databinding.FragmentDetailBinding
import com.example.bluetoothproject.models.ConnectedDeviceModel
import com.example.bluetoothproject.models.Service
import com.example.bluetoothproject.utils.constants.Companion.CLICKED_DEVİCE_ADDRESS
import com.example.bluetoothproject.utils.constants.Companion.CLICKED_DEVİCE_NAME
import com.example.bluetoothproject.utils.constants.Companion.MY_UUID
import com.example.bluetoothproject.viewmodels.DetailFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class DetailFragment : Fragment() {

    // Intent request codes
    private val REQUEST_CONNECT_DEVICE_SECURE = 1
    private val REQUEST_CONNECT_DEVICE_INSECURE = 2
    private val REQUEST_ENABLE_BT = 3


    lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothReceiver: BroadcastReceiver
    lateinit var bluetoothManager: BluetoothManager


    val viewModel by viewModel<DetailFragmentViewModel>()

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.title = getString(R.string.details_label)
        }
        setHasOptionsMenu(true)

        bluetoothManager =
            (activity as AppCompatActivity).getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        binding.tvDeviceName.text = CLICKED_DEVİCE_NAME
        binding.tvDeviceAddress.text = CLICKED_DEVİCE_ADDRESS

        //daha once datada kayıtlı ise bilgileri otomatik yazdırılacak. Hata alındı
//        dataControlInLocal(CLICKED_DEVİCE_ADDRESS)

        binding.btnConnect.setOnClickListener {
            Log.i("tag_flow", "DetailFragment => onCreateView connect clicked")

            try {
                val address = CLICKED_DEVİCE_ADDRESS
                val device = bluetoothAdapter!!.getRemoteDevice(address)
                Log.i("tag_flow", "DetailFragment => connect => device: $device")
                ConnectThread(device).start()
            } catch (e: Exception) {
                Log.e("tag_error", "Hata : $e")
            }

        }

        binding.btnDisconnect.setOnClickListener {
            Log.i("tag_flow", "DetailFragment => onCreateView disconnect clicked")

            val address = CLICKED_DEVİCE_ADDRESS
            // Get the BluetoothDevice object
            val device = bluetoothAdapter!!.getRemoteDevice(address)

        }

        val textView: TextView = binding.tvDetailMessage
        viewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })






        return view
    }

    fun dataControlInLocal(address: String) {

//        BluetoothHistoryDB2.databaseWriteExecutor.execute {
//
//            val device =
//                BluetoothHistoryDB2.getDatabase(activity).devicesDao().searchDevice(address)
//            if (device != null) {
//                activity?.runOnUiThread {
//                    val connectedDeviceModel = device
//                    binding.tvUuid.text = connectedDeviceModel.services[0].uuid
//                    binding.tvCharacteristics.text =
//                        connectedDeviceModel.services[0].characteristics.toString()
//                    binding.tvUuid.visibility = View.VISIBLE
//                    binding.tvCharacteristics.visibility = View.VISIBLE
//                }
//            }
//
//        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i(
            "tag_flow",
            "DetailFragment => onOptionsItemSelected => itemId : ${item.itemId} $item"
        )
        return when (item.itemId) {
            android.R.id.home -> goToBack()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goToBack(): Boolean {
        Log.i("tag_flow", "DetailFragment => goToBack")
        (activity as AppCompatActivity).onBackPressed()
        return true
    }

    fun getMockConnectedDevice(): ConnectedDeviceModel {
        //gercek veriler alındıgında kullanılmayacak
        val random = (0..100).random()
        val service: Service = Service(listOf("1characteristic", "2characteristic"), "mockUUID")
        val connectedDeviceModel = ConnectedDeviceModel(
            random, "MOCKADDRESS$random", "MOCKNAME$random",
            arrayListOf(service)
        )
        Log.i("tag_mock", "mock connected device= $service")
        return connectedDeviceModel
    }

    fun bilgileriEkranaGetir(connectedDeviceModel: ConnectedDeviceModel) {


        activity?.runOnUiThread {
            binding.tvUuid.text = connectedDeviceModel.services[0].uuid
            binding.tvCharacteristics.text =
                connectedDeviceModel.services[0].characteristics.toString()
            binding.tvUuid.visibility = View.VISIBLE
            binding.tvCharacteristics.visibility = View.VISIBLE
        }


    }

    fun localDatayaKayıtEt(connectedDeviceModel: ConnectedDeviceModel) {

        viewModel.saveLocalDataBase(connectedDeviceModel, BluetoothHistoryDB2.getDatabase(activity))
        Log.i("tag_flow", "DetailFragment => localDatayaKayıtEt ")


    }


    //              Asagısı Socket baglantısı icin hazırlanmıştı                ///
///////////////////////////////////////////////////////////////////////////////////////////////////

    private inner class AcceptThread : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
//            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("NAME", MY_UUID)
            bluetoothAdapter?.listenUsingRfcommWithServiceRecord("NAME", MY_UUID)
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e("TAG", "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
//                    manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e("TAG", "Could not close the connect socket", e)
            }
        }
    }


    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
//            device.createRfcommSocketToServiceRecord(MY_UUID)
            device.createInsecureRfcommSocketToServiceRecord(MY_UUID)
        }

        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->

                // Not: baglantıda hata alıyorum. Şimdilik mock veriler ile programa devam ediyorum
//                socket.connect()

                //bağlantı basarılı, soket kullanabilirsin...

                val device = getMockConnectedDevice()

                bilgileriEkranaGetir(device)

                viewModel.sendDeviceToApi(device)



                localDatayaKayıtEt(device)

            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e("TAG", "Could not close the client socket", e)
            }
        }
    }

    private fun ensureDiscoverable() {
        if (bluetoothAdapter!!.scanMode !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE
        ) {
            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            startActivity(discoverableIntent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CONNECT_DEVICE_SECURE ->                 // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, true)
                }
            REQUEST_CONNECT_DEVICE_INSECURE ->                 // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, false)
                }
            REQUEST_ENABLE_BT ->                 // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d("TAG", "BT not enabled")
                    this.activity?.finish()
                }
        }
    }


    var serviceUUIDsList: ArrayList<UUID> = ArrayList()
    var characteristicUUIDsList: ArrayList<UUID> = ArrayList()
    var descriptorUUIDsList: ArrayList<UUID> = ArrayList()

    //    private fun initScanning(bleScanner: BluetoothLeScannerCompat) {
    private fun initScanning(bleScanner: BluetoothLeScanner) {
        bleScanner.startScan(getScanCallback())
    }

    private fun getScanCallback(): ScanCallback? {
        return object : ScanCallback() {
            override fun onScanResult(callbackType: Int, scanResult: ScanResult) {
                super.onScanResult(callbackType, scanResult)
                serviceUUIDsList = getServiceUUIDsList(scanResult) as ArrayList<UUID>
            }
        }
    }

    private fun getServiceUUIDsList(scanResult: ScanResult): List<UUID> {
        val parcelUuids: List<ParcelUuid> = scanResult.getScanRecord()!!.getServiceUuids()
        val serviceList: MutableList<UUID> = ArrayList()
        for (i in parcelUuids.indices) {
            val serviceUUID: UUID = parcelUuids[i].uuid
            if (!serviceList.contains(serviceUUID)) serviceList.add(serviceUUID)
        }
        return serviceList
    }

    private fun defineCharAndDescrUUIDs(bluetoothGatt: BluetoothGatt) {
        val servicesList = bluetoothGatt.services
        for (i in servicesList.indices) {
            val bluetoothGattService = servicesList[i]
            if (serviceUUIDsList.contains(bluetoothGattService.uuid)) {
                val bluetoothGattCharacteristicList = bluetoothGattService.characteristics
                for (bluetoothGattCharacteristic in bluetoothGattCharacteristicList) {
                    characteristicUUIDsList.add(bluetoothGattCharacteristic.uuid)
                    val bluetoothGattDescriptorsList = bluetoothGattCharacteristic.descriptors
                    for (bluetoothGattDescriptor in bluetoothGattDescriptorsList) {
                        descriptorUUIDsList.add(bluetoothGattDescriptor.uuid)
                    }
                }
            }
        }
    }


}