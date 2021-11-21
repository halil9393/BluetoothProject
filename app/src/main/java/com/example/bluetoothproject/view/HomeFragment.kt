package com.example.bluetoothproject.view

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetoothproject.R
import com.example.bluetoothproject.adapter.DevicesRecyclerViewAdapter
import com.example.bluetoothproject.databinding.FragmentHomeBinding
import com.example.bluetoothproject.models.FondDeviceModel
import com.example.bluetoothproject.utils.constants.Companion.CLICKED_DEVİCE_ADDRESS
import com.example.bluetoothproject.utils.constants.Companion.CLICKED_DEVİCE_NAME
import com.example.bluetoothproject.viewmodels.HomeFragmentViewModel
import com.example.bluetoothproject.viewmodels.HomeFragmentViewModel.UiState.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeFragmentViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothReceiver: BroadcastReceiver
    lateinit var bluetoothManager: BluetoothManager

    private val recyclerViewAdapter = DevicesRecyclerViewAdapter(arrayListOf())
    var deviceList: ArrayList<FondDeviceModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            it.title = getString(R.string.app_name)
        }


        bluetoothManager =
            (activity as AppCompatActivity).getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        initializeReceiver()

        initializeButtons()

        observeWithFlow()

        initializeRecycler()

        return view
    }

    private fun initializeRecycler() {

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = recyclerViewAdapter
        recyclerViewAdapter.mySetOnItemClickListener(object :
            DevicesRecyclerViewAdapter.MyOnItemClickListener {
            override fun myOnItemClick(itemView: View?, position: Int) {
                Log.i("tag_flow", "tiklandi ${deviceList.get(position)}")

                CLICKED_DEVİCE_NAME = deviceList.get(position).deviceName.toString()
                CLICKED_DEVİCE_ADDRESS = deviceList.get(position).deviceAddress.toString()

                (activity as AppCompatActivity).supportFragmentManager.commit {
                    replace<DetailFragment>(R.id.nav_host_fragment, null, null)
                    addToBackStack("homeToDetail")
                }

            }
        })
        recyclerViewAdapter.updateDataList(deviceList)

    }


    private fun observeWithFlow() {

        val textView: TextView = binding.tvHomeMessage
        viewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })


        lifecycleScope.launch {

            viewModel.deviceListState.collectLatest {
                when (it) {

                    is Success -> {
                        Log.i("tag_flow", "HomeFragment => UiState.Succes")
                    }

                    is Loading -> {
                        Log.i("tag_flow", "HomeFragment => UiState.Loading")
                    }

                    is Fail -> {
                        Log.i("tag_flow", "HomeFragment => UiState.Fail")
                    }

                }
            }
        }

    }

    private fun initializeButtons() {
        binding.btnScan.setOnClickListener {
            if (bluetoothAdapter!!.scanMode !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE
            ) {
                val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                startActivity(discoverableIntent)
            }

            bluetoothAdapter.startDiscovery()
        }


        binding.btnOpenBle.setOnClickListener {
            bluetoothAdapter.enable()
        }

        binding.btnCloseBle.setOnClickListener {
            bluetoothAdapter.disable()
        }


    }

    private fun initializeReceiver() {

        bluetoothReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action


                when (action) {

                    BluetoothAdapter.ACTION_REQUEST_ENABLE -> {
                        Log.i("tag_flow", "BluetoothReceiver => onReceive => ACTION_REQUEST_ENABLE")
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                        Log.i(
                            "tag_flow",
                            "BluetoothReceiver => onReceive => ACTION_DISCOVERY_STARTED"
                        )
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        Log.i(
                            "tag_flow",
                            "BluetoothReceiver => onReceive => ACTION_DISCOVERY_FINISHED"
                        )
                    }

                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE -> {
                        Log.i(
                            "tag_flow",
                            "BluetoothReceiver => onReceive => ACTION_REQUEST_DISCOVERABLE"
                        )
                    }

                    BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                        Log.i(
                            "tag_flow",
                            "BluetoothReceiver => onReceive => ACTION_SCAN_MODE_CHANGED"
                        )
                    }

                    BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                        Log.i(
                            "tag_flow",
                            "BluetoothReceiver => onReceive => ACTION_CONNECTION_STATE_CHANGED"
                        )
                    }

                    BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED -> {
                        Log.i(
                            "tag_flow",
                            "BluetoothReceiver => onReceive => ACTION_LOCAL_NAME_CHANGED"
                        )
                    }

                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        Log.i("tag_flow", "BluetoothReceiver => onReceive => ACTION_STATE_CHANGED")
                    }

                    BluetoothDevice.ACTION_FOUND -> {
                        Log.i("tag_flow", "BluetoothReceiver => onReceive => ACTION_FOUND")

                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        if (device?.bondState != BluetoothDevice.BOND_BONDED) {

                            Log.i("tag_flow", "founded : ${device?.name}\n ${device?.address}")

                            if(!deviceList.contains(FondDeviceModel(device?.name, device!!.address)))
                            deviceList.add(FondDeviceModel(device?.name, device?.address))

                            recyclerViewAdapter.updateDataList(deviceList)
                        }
                    }
                }
            }
        }


        val intentFilter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
            addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
            addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        }
        (activity as AppCompatActivity).registerReceiver(bluetoothReceiver, intentFilter)

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}

