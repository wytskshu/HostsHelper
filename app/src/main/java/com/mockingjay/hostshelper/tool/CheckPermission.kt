package com.mockingjay.hostshelper.tool

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log

/**
 * Created by Mockingjay on 2017/8/24.
 */
class CheckPermission{
    companion object {
        private var PREMISSIONS= arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
        )

        fun checkGetPermission(context: Context) {
            if (ActivityCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //未授权，提起权限申请
                Log.d("TAG===","未授权")
                ActivityCompat.requestPermissions(context as Activity, PREMISSIONS,1)
            } else {
                Log.d("TAG===","已授权")
                //权限已授权，功能操作
            }

        }
    }
}