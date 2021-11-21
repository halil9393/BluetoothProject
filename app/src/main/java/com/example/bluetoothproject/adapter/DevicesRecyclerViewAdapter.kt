package com.example.bluetoothproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothproject.R
import com.example.bluetoothproject.models.FondDeviceModel

class DevicesRecyclerViewAdapter(val devicesList:ArrayList<FondDeviceModel>) :
RecyclerView.Adapter<DevicesRecyclerViewAdapter.ViewHolder>(){

        private var myListener: MyOnItemClickListener? = null

    interface MyOnItemClickListener {
        fun myOnItemClick(itemView: View?, position: Int)
    }

    fun mySetOnItemClickListener(listener: MyOnItemClickListener?) {
        myListener = listener
    }


    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        var name : TextView = view.findViewById(R.id.tvName)
        var address : TextView = view.findViewById(R.id.tvAdress)

        fun setData(fondDeviceModel: FondDeviceModel) {
            name.text = fondDeviceModel.deviceName
            address.text = fondDeviceModel.deviceAddress
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_fond_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fondDeviceModel : FondDeviceModel = devicesList[position]
        holder.setData(fondDeviceModel)
        holder.itemView.setOnClickListener { myListener!!.myOnItemClick(holder.itemView,position) }
    }

    override fun getItemCount(): Int {
        return devicesList.size
    }

    //    // resfresh için kullanılacak
    fun updateDataList(newDataList: List<FondDeviceModel>) {
        devicesList.clear()
        devicesList.addAll(newDataList)
        notifyDataSetChanged()
    }
}