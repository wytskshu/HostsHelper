package com.mockingjay.hostshelper.tool

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.mockingjay.hostshelper.DataBaseID
import com.mockingjay.hostshelper.HostsItem

/**
 * Created by Mockingjay on 2017/8/22.
 */
class DbManager {
    companion object {
        val TAG="TAG===DbManager"
        private var helper: MyDataBase? = null
        fun getIntance(context: Context): MyDataBase {
            if (helper == null) {
                helper = MyDataBase(context, DataBaseID.DATABASE_NAME, null, DataBaseID.DATABASE_VERSION)
            }
            return helper as MyDataBase
        }

        fun getAllData(cursor: Cursor): ArrayList<HostsItem> {
            val list = ArrayList<HostsItem>()
            while (cursor.moveToNext()) {
               var mhostsItem=HostsItem()
                mhostsItem.id= cursor.getInt(cursor.getColumnIndex(DataBaseID.KEY_ID))
                mhostsItem.name= cursor.getString(cursor.getColumnIndex(DataBaseID.KEY_NAME))
                mhostsItem.InternetAddress= cursor.getString(cursor.getColumnIndex(DataBaseID.KEY_INTERNETADDRESS))
                mhostsItem.contentpath= cursor.getString(cursor.getColumnIndex(DataBaseID.KEY_CONTENTPATH))
                mhostsItem.Enable= cursor.getInt(cursor.getColumnIndex(DataBaseID.KEY_Enable))
                mhostsItem.keyflag= cursor.getString(cursor.getColumnIndex(DataBaseID.KEY_KEYFLAG))
                list.add(mhostsItem)
            }
            return list
        }

        fun addDataBase(helper: MyDataBase, datas: Array<String>, enable: Int): Long {
            var db = helper.writableDatabase
            var value = ContentValues()
            value.put(DataBaseID.KEY_NAME, datas[0])
            value.put(DataBaseID.KEY_INTERNETADDRESS, datas[1])
            value.put(DataBaseID.KEY_CONTENTPATH, datas[2])
            value.put(DataBaseID.KEY_LASTTIME, datas[3])
            value.put(DataBaseID.KEY_KEYFLAG, datas[4])
            value.put(DataBaseID.KEY_Enable, enable)
            var longrs = db.insert(DataBaseID.TABLE_NAME, null, value)
            db.close()
            return longrs

        }

        fun getLastDataBase(cursor: Cursor): HostsItem {

         var mhostsItem=HostsItem()
            cursor.moveToLast()
            mhostsItem.id= cursor.getInt(cursor.getColumnIndex(DataBaseID.KEY_ID))
            mhostsItem.name= cursor.getString(cursor.getColumnIndex(DataBaseID.KEY_NAME))
            mhostsItem.InternetAddress= cursor.getString(cursor.getColumnIndex(DataBaseID.KEY_INTERNETADDRESS))
            mhostsItem.contentpath= cursor.getString(cursor.getColumnIndex(DataBaseID.KEY_CONTENTPATH))
            mhostsItem.Enable= cursor.getInt(cursor.getColumnIndex(DataBaseID.KEY_Enable))
            mhostsItem.keyflag= cursor.getString(cursor.getColumnIndex(DataBaseID.KEY_KEYFLAG))
            return mhostsItem
        }

        fun updateId(helper: MyDataBase, newdata: ContentValues, id: Int) {
            var db = helper.writableDatabase
            db.update(DataBaseID.TABLE_NAME, newdata, "_id=?", arrayOf(id.toString()))
            db.close()
        }

        fun delete(helper: MyDataBase, id: Int) {
            var db = helper.writableDatabase
            db.delete(DataBaseID.TABLE_NAME, "_id=?", arrayOf(id.toString()))
            db.close()
        }

        fun deleteAll(helper: MyDataBase) {
            var db = helper.writableDatabase
            db.delete(DataBaseID.TABLE_NAME, null, null)
            db.close()
        }

        fun updateWhere(helper: MyDataBase, sqlstr: String) {
            var db = helper.writableDatabase
//            db.update(DataBaseID.TABLE_NAME, newdata, where, arg)
            db.execSQL(sqlstr)
            db.close()
        }
    }


}