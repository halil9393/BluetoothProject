package com.example.bluetoothproject.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetoothproject.R
import com.example.bluetoothproject.adapter.DevicesRecyclerViewAdapter
import com.example.bluetoothproject.data.BluetoothHistoryDB2
import com.example.bluetoothproject.data.DevicesDao
import com.example.bluetoothproject.databinding.FragmentHistoryBinding
import com.example.bluetoothproject.models.FondDeviceModel
import com.example.bluetoothproject.utils.constants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment() {


    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val recyclerViewAdapter = DevicesRecyclerViewAdapter(arrayListOf())
    private var deviceList: ArrayList<FondDeviceModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater,container,false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            it.title = getString(R.string.app_name)
        }



        BluetoothHistoryDB2.databaseWriteExecutor.execute {

            val liste = BluetoothHistoryDB2.getDatabase(activity).devicesDao().getFondDeviceHistory()

            if(liste != null && liste.size > 0){
                for (i in liste) Log.i("tag_flow","gelen liste : $i")
            }



            activity?.runOnUiThread {
                deviceList = liste as ArrayList<FondDeviceModel>
                recyclerViewAdapter.updateDataList(deviceList)

            }


        }


        initializeRecycler()







        return view
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initializeRecycler() {

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = recyclerViewAdapter
        recyclerViewAdapter.mySetOnItemClickListener(object :
            DevicesRecyclerViewAdapter.MyOnItemClickListener {
            override fun myOnItemClick(itemView: View?, position: Int) {
                Log.i("tag_flow", "tiklandi ${deviceList.get(position)}")

                constants.CLICKED_DEVİCE_NAME = deviceList.get(position).deviceName.toString()
                constants.CLICKED_DEVİCE_ADDRESS = deviceList.get(position).deviceAddress

                (activity as AppCompatActivity).supportFragmentManager.commit {
                    replace<DetailFragment>(R.id.nav_host_fragment, null, null)
                    addToBackStack("homeToDetail")
                }

            }
        })
        recyclerViewAdapter.updateDataList(deviceList)

    }

}