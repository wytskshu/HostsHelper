package com.mockingjay.hostshelper.tool

import java.text.SimpleDateFormat
import java.util.*




/**
 * Created by Mockingjay on 2017/8/23.
 */
class Time{
    companion object {
        fun getNowTime(): String{
            var  longTime=System.currentTimeMillis()
            var dataFormat=SimpleDateFormat("yyyy-MM-dd H:mm:s",Locale.getDefault())
            var date=dataFormat.format(longTime)
            return date
        }
        fun getNowtimeNub(): String{
            //获取当前时间戳
            val timeStamp = System.currentTimeMillis()
            val time = stampToDate(timeStamp)
            return dateToStamp(time)
        }
        private fun stampToDate(timeMillis: Long): String {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = Date(timeMillis)
            return simpleDateFormat.format(date)
        }


       private fun dateToStamp(time: String): String {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = simpleDateFormat.parse(time)
            val ts = date.time
            return ts.toString()
        }

    }

}