package com.mockingjay.hostshelper.tool

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.mockingjay.hostshelper.DataBaseID


/**
 * Created by Mockingjay on 2017/8/22.
 */
class MyDataBase(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    val TAG = "MyDataBase"
    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "Database onCreate")
        var sql="create table "+DataBaseID.TABLE_NAME+"(_id Integer primary key AUTOINCREMENT,name TEXT,InternetAddress TEXT,contentpath TEXT,lasttime TEXT,Enable Integer,keyflag TEXT)"
        db!!.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "Database onUpgrade")
    }


}