package com.mockingjay.hostshelper.UI

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import com.mockingjay.hostshelper.DataBaseID
import com.mockingjay.hostshelper.HostsItem
import com.mockingjay.hostshelper.R
import com.mockingjay.hostshelper.adapter.MainAdapter
import com.mockingjay.hostshelper.tool.*
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import tool.AlertDialog


class MainActivity : AppCompatActivity() {
    val TAG = "TAG===MainActivity"
    val REQUESTCODE = 1
    val MSG_DELETEALL_OK = 22
    lateinit var mMainAdaper: MainAdapter
    lateinit var helper: MyDataBase
    lateinit var mhandle: Handler
    lateinit var mlists: ArrayList<HostsItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        helper = DbManager.getIntance(this)
        if (isFirstStart()) {
            Log.d(TAG, "程序是第一次运行")
            File.backSysHosts(File.getAppfiles(this))
        } else {
            Log.d(TAG, "程序非第一次运行")
        }
        bt_update_all.setOnClickListener {
            var hostsintent = File.getTextFileIntent(java.io.File("/system/etc/hosts"))
            startActivity(hostsintent)
//            var str= arrayListOf<String>()
//            str.add("grep -n \\#12345 /system/etc/hosts")
//            Shell.HandleShell(str,true)

        }
        bt_delete_all.setOnClickListener {
            AlertDialog.alertshure(this, "会删除所有的保存数据,不会删除备份", {
                Thread(Runnable {
                    deleteLoaclFiles()
                    DbManager.deleteAll(helper)
                }).start()
            })


        }
        bt_defult.setOnClickListener {
            AlertDialog.alertshure(this, "会将系统Hosts恢复默认,并关闭启用的方案", {
                File.HostsToDefault(File.getAppfiles(this))
                var newdata = ContentValues()
                newdata.put(DataBaseID.KEY_Enable, 0)
                DbManager.updateWhere(helper, "update " + DataBaseID.TABLE_NAME + " set Enable='0' where Enable='1'")
                mMainAdaper.RecUiToDefult()
                Snackbar.make(main_coordinatorLayout, "恢复默认hotsts成功", Snackbar.LENGTH_LONG)
                        .show()
            })

        }
        main_floatingActionButton.setOnClickListener {
            var intent = Intent(this@MainActivity, MainAddActivity::class.java)
            intent.putExtra("src", "add")
            startActivityForResult(intent, REQUESTCODE)
        }
        mhandle = Handler { message ->
            when (message.what) {
                MSG_DELETEALL_OK -> {
                    mMainAdaper.removeAllItem()
                }

            }
            return@Handler true
        }
        mlists = initData()
        initView(mlists)

    }

    fun initData(): ArrayList<HostsItem> {
        var db = helper.writableDatabase
        var sql = "select * from " + DataBaseID.TABLE_NAME
        var cursor = db.rawQuery(sql, null)
        var lists = DbManager.getAllData(cursor)
        db.close()
        return lists
    }

    fun initView(data: Any) {
        main_recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mMainAdaper = MainAdapter(this, data as ArrayList<HostsItem>, helper)
        main_recyclerView.addItemDecoration(DefaultItemDecoration(Color.RED, 2, 2))
        var mswipmenu = SwipeMenuCreator { swipeLeftMenu, swipeRightMenu, viewType ->


            var openItem = SwipeMenuItem(this)
            openItem.text = "切换"
            openItem.width = 120
            openItem.setTextColor(Color.WHITE)
            openItem.height = ViewGroup.LayoutParams.MATCH_PARENT
            openItem.setBackgroundColor(Color.parseColor("#F2AE72"))
            swipeRightMenu.addMenuItem(openItem)

            var deleteItem = SwipeMenuItem(this)
            deleteItem.width = 120
            deleteItem.text = "删除"
            deleteItem.setTextColor(Color.WHITE)
            deleteItem.height = ViewGroup.LayoutParams.MATCH_PARENT
            deleteItem.setBackgroundColor(Color.RED)
            swipeRightMenu.addMenuItem(deleteItem)
        }
        main_recyclerView.setSwipeMenuCreator(mswipmenu)
        main_recyclerView.setSwipeMenuItemClickListener {
            it.closeMenu()
            val direction = it.getDirection() // 左侧还是右侧菜单。
            val adapterPosition = it.getAdapterPosition() // RecyclerView的Item的position。
            val menuPosition = it.getPosition() // 菜单在RecyclerView的Item中的Position。
            val enable = mlists[adapterPosition].Enable
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                //Toast.makeText(this@MainActivity, "list第$adapterPosition; 右侧菜单第$menuPosition", Toast.LENGTH_SHORT).show()
                when (menuPosition) {
                    0 -> {
                        if (enable == 0) {
                            AlertDialog.alertshure(this@MainActivity, "确定要启用吗(不需要重启)", {
                                if (enable == 0) {
                                    //Toast.makeText(this, "正在启用", Toast.LENGTH_SHORT).show()
                                   // Log.d(TAG,mlists[adapterPosition].contentpath)
                                    var lists = File.readFileByLinesToListToShell(mlists[adapterPosition].contentpath, mlists[adapterPosition].keyflag)
                                    Shell.HandleShell(lists, true)
                                    var value = ContentValues()
                                    value.put("Enable", 1)
                                    DbManager.updateId(helper, value, mlists[adapterPosition].id)
                                    mlists[adapterPosition].Enable = 1
                                    mMainAdaper.refreshItem(adapterPosition)
                                    Toast.makeText(this, "已经写入系统", Toast.LENGTH_SHORT).show()
                                }
                            })

                        } else if (enable == 1) {
                            AlertDialog.alertshure(this@MainActivity, "确定要关闭吗,只会在Hosts中删除此方案,对其它无影响", {
                                Toast.makeText(this, "正在关闭", Toast.LENGTH_SHORT).show()
                                Shell.deleteThisHosts(mlists[adapterPosition].keyflag)
                                var value = ContentValues()
                                value.put("Enable", 0)
                                DbManager.updateId(helper, value, mlists[adapterPosition].id)
                                mlists[adapterPosition].Enable = 0
                                mMainAdaper.refreshItem(adapterPosition)
                                Toast.makeText(this, "已经从系统Hosts中移除此方案", Toast.LENGTH_SHORT).show()
                            })

                        }
                    }
                    1 -> {
                        //删除
                        if (mlists[adapterPosition].Enable == 1) {
                            Toast.makeText(this, "请先关闭此方案再删除", Toast.LENGTH_SHORT).show()
                            return@setSwipeMenuItemClickListener
                        }
                        AlertDialog.alertshure(this, "确定要删除?", {
                            Log.d(TAG, "position" + adapterPosition)
                            DbManager.delete(helper, mlists[adapterPosition].id)
                            File.deleteFile(mlists[adapterPosition].contentpath)
                            mMainAdaper.removeItem(adapterPosition)
                            Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show()
                        })
                    }
                }
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(this@MainActivity, "list第$adapterPosition; 左侧菜单第$menuPosition", Toast.LENGTH_SHORT).show()
            }
        }
        main_recyclerView.adapter = mMainAdaper
    }

    fun deleteLoaclFiles() {
        var db = helper.writableDatabase
        var sql = "select * from " + DataBaseID.TABLE_NAME
        var cursor = db.rawQuery(sql, null)
        var lists = DbManager.getAllData(cursor)
        for (list in lists) {
            var path = list.contentpath
            File.deleteFile(path)
        }
        db.close()
        var msg = Message()
        msg.what = MSG_DELETEALL_OK
        mhandle.sendMessage(msg)
        Snackbar.make(main_coordinatorLayout, "已经删除所有数据", Snackbar.LENGTH_LONG)
                .show()
    }

    override fun onResume() {

        if (getSDKVersionNumber() >= 23) {
            CheckPermission.checkGetPermission(this)
        }

        super.onResume()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            1 -> {
                Log.d(TAG, "直接返回back")
            }
            2 -> {
                Log.d(TAG, "已经添加数据,刷新UI")
                Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show()
                var db = helper.writableDatabase
                var sql = "select * from " + DataBaseID.TABLE_NAME
                var cursor = db.rawQuery(sql, null)
                var mapres = DbManager.getLastDataBase(cursor)
                db.close()
                mMainAdaper.addItem(mapres)
            }
            3 -> {
                Log.d(TAG, "修改数据")
                var position = data!!.getIntExtra("position", -1)
                var name = data.getStringExtra("changename")
                var lasttime = data.getStringExtra("lasttime")
                var address = data.getStringExtra("address")
                mMainAdaper.updateItem(position, name, address, lasttime)
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isFirstStart(): Boolean {
        val sharedPreferences = this.getSharedPreferences("infoFile", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("first_start", true)
        val editor = sharedPreferences.edit()
        if (isFirstRun) {
            // 当程序是第二次运行时
            editor.putBoolean("first_start", false)
            editor.commit()
        }
        return isFirstRun
    }


    fun getSDKVersionNumber(): Int {
        var sdkVersion: Int
        try {
            sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK)!!
        } catch (e: NumberFormatException) {
            sdkVersion = 0
        }

        return sdkVersion
    }
}
