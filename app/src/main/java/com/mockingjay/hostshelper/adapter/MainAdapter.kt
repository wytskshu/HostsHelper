package com.mockingjay.hostshelper.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.mockingjay.hostshelper.HostsItem
import com.mockingjay.hostshelper.R
import com.mockingjay.hostshelper.UI.MainAddActivity
import com.mockingjay.hostshelper.tool.MyDataBase


/**
 * Created by Mockingjay on 2017/8/22.
 */
class MainAdapter(context: Activity, list: ArrayList<HostsItem>, helper: MyDataBase) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mlist = list
    var mcontext = context
    //var mhelper = helper
    val TAG = "TAG===MainAdapter"

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        var mholder = holder as MyHolder
        var name = mlist[position].name
        var lasttime = mlist[position].lasttime
        var address = mlist[position].InternetAddress
        var id = mlist[position].id
       // var keyflag = mlist[position].keyflag
        var enable = mlist[position].Enable
        var contentpath = mlist[position].contentpath
        mholder.tv_title.text = name
        mholder.tv_time.text = lasttime
        mholder.tv_address.text = address
        if (enable == 1) {
            mholder.bt_open.setBackgroundColor(Color.parseColor("#12CC94"))
            mholder.bt_open.text = "已启用"
            mholder.fatherView.setBackgroundColor(Color.parseColor("#A6ED8E"))

        } else if (enable == 0) {
            mholder.bt_open.setBackgroundColor(Color.parseColor("#707070"))
            mholder.fatherView.setBackgroundColor(Color.parseColor("#ffffff"))
        }

        mholder.fatherView.setOnClickListener {
            if (mholder.bt_open.text == "关闭") {
                Toast.makeText(mcontext, "请先关闭再进行修改编辑", Toast.LENGTH_SHORT).show()
            } else {
                var intent = Intent(mcontext, MainAddActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("src", "edit")
                intent.putExtra("name", name)
                intent.putExtra("lasttime", lasttime)
                intent.putExtra("address", address)
                intent.putExtra("_id", id)
                intent.putExtra("contentpath", contentpath)
                mcontext.startActivityForResult(intent, 1)
            }


        }

    }

    override fun getItemCount(): Int {
        return mlist.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var itemview = LayoutInflater.from(mcontext).inflate(R.layout.mainlist_item, parent, false)

        return MyHolder(itemview)
    }

    fun addItem(data: HostsItem) {
        mlist.add(data)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        mlist.removeAt(position)
        notifyDataSetChanged()
        //notifyItemRemoved(position)
        //notifyItemRangeChanged(position, mlist.size)
    }

    fun removeAllItem() {
        mlist.clear()
        notifyDataSetChanged()
    }

    fun refreshItem(position: Int) {
        notifyItemChanged(position)
    }

    fun updateItem(position: Int, name: String, address: String, lasttime: String) {
        mlist[position].name = name
        mlist[position].InternetAddress = address
        mlist[position].lasttime = lasttime
        notifyItemChanged(position)
    }

    fun RecUiToDefult() {
        for (list in mlist) {
            list.Enable = 0
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return mlist[position].Enable
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_title: TextView = itemView.findViewById<TextView>(R.id.text_title)
        var tv_time: TextView = itemView.findViewById<TextView>(R.id.text_time)
        var tv_address: TextView = itemView.findViewById<TextView>(R.id.text_address)
        var bt_open: TextView = itemView.findViewById<TextView>(R.id.bt_open)
        var fatherView: View = itemView.findViewById<View>(R.id.father_main)


    }

}
