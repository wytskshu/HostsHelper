package com.mockingjay.hostshelper.UI

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.mockingjay.hostshelper.DataBaseID
import com.mockingjay.hostshelper.R
import com.mockingjay.hostshelper.tool.DbManager
import com.mockingjay.hostshelper.tool.File
import com.mockingjay.hostshelper.tool.MyDataBase
import com.mockingjay.hostshelper.tool.Time
import kotlinx.android.synthetic.main.activity_main_add.*
import okhttp3.*
import java.io.IOException

class MainAddActivity : AppCompatActivity() {
    val TAG = "TAG===MainAddActivity"
    val RESULT_BACK = 1
    val RESULT_OK = 2
    val RESULT_EDIT = 3
    var startmodel = 0
    var editid: Int = -1
    var position = -1
     var bigcontent: String=""
    lateinit var contentpath: String
    lateinit var helper: MyDataBase
    lateinit var mhandle: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_add)
        setSupportActionBar(add_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        add_toolbar.setNavigationOnClickListener {
            setResult(RESULT_BACK)
            finish()
        }
        bt_sync.setOnClickListener {
            if (edit_address.text.toString() == "") {
                Snackbar.make(add_constraintLayout, "你还没有输入地址呢", Snackbar.LENGTH_SHORT)
                        .show()
            } else {
                Toast.makeText(this, "正在同步....", Toast.LENGTH_SHORT).show()
                Thread(Runnable {
                    synchttp()
                }).start()
            }
        }
        mhandle = Handler(Looper.getMainLooper()) { message: Message ->
            //edit_content.setText(message.obj.toString(), TextView.BufferType.NORMAL)
            bigcontent = message.obj.toString()
            text_time.text = Time.getNowTime()
            Toast.makeText(this, "同步完成", Toast.LENGTH_SHORT).show()
            return@Handler true
        }
        helper = DbManager.getIntance(this)
        var flag = intent.getStringExtra("src")
        Log.d(TAG, flag)
        if (flag == "add") {
            //添加空白数据
            startmodel = 0
        } else if (flag == "edit") {
            //编辑数据
            startmodel = 1
            Thread(Runnable {
                initdata()
            }).start()

        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_yes -> {
                Log.d(TAG, "保存")
                if (edit_name.text.toString() == "") {
                    Snackbar.make(add_constraintLayout, "名称不能为空", Snackbar.LENGTH_SHORT)
                            .show()
                } else {
                    if (startmodel == 0) {
                        Toast.makeText(this, "正在保存....", Toast.LENGTH_SHORT).show()
                        var timenub = Time.getNowtimeNub()
                        if (edit_address.text.toString() != "") {
                            File.writeIntofiles(this, timenub, bigcontent)
                        } else {
                            File.writeIntofiles(this, timenub, edit_content.text.toString())
                        }

                        DbManager.addDataBase(helper, arrayOf(edit_name.text.toString(), edit_address.text.toString(),
                                File.getAppfiles(this) + "/" + timenub, Time.getNowTime(), "#" + timenub
                        ), 0)
                        setResult(RESULT_OK)
                        finish()

                    } else if (startmodel == 1) {
                        Toast.makeText(this, "正在保存....", Toast.LENGTH_SHORT).show()
                        if (edit_address.text.toString() != "") {
                            File.saveToFile(contentpath, bigcontent)
                        } else {
                            File.saveToFile(contentpath, edit_content.text.toString())
                        }

                        var newdata = ContentValues()
                       var time = text_time.text.toString()

                        newdata.put(DataBaseID.KEY_NAME, edit_name.text.toString())
                        newdata.put(DataBaseID.KEY_INTERNETADDRESS, edit_address.text.toString())
                        newdata.put(DataBaseID.KEY_LASTTIME, time)
                        DbManager.updateId(helper, newdata, editid)
                        var mintent = Intent()
                        var str = edit_name.text.toString()
                        mintent.putExtra("changename", str)
                        mintent.putExtra("address", edit_address.text.toString())
                        mintent.putExtra("lasttime", time)
                        mintent.putExtra("position", position)
                        Log.d(TAG, "设置回复代码")
                        setResult(RESULT_EDIT, mintent)
                        finish()

                    }

                }

            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        setResult(RESULT_BACK)
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mian_add_menu, menu)
        return true
    }

    fun synchttp() {

        var client = OkHttpClient()
        var request = Request.Builder().url(edit_address.text.toString()).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call?, response: Response?) {
                Log.d(TAG, "请求成功回调")
                var msg = Message()
                msg.what = 0
                msg.obj = response!!.body()!!.string()
                mhandle.sendMessage(msg)
            }

        })
    }

    fun initdata() {
        edit_name.setText(intent.getStringExtra("name"), TextView.BufferType.NORMAL)
        edit_address.setText(intent.getStringExtra("address"), TextView.BufferType.NORMAL)
        text_time.text =  intent.getStringExtra("lasttime")
        contentpath = intent.getStringExtra("contentpath")
        editid = intent.getIntExtra("_id", -1)
        position = intent.getIntExtra("position", -1)
        //synchttp()
    }


}
