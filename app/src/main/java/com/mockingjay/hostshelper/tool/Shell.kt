package com.mockingjay.hostshelper.tool

import android.util.Log
import com.mockingjay.hostshelper.lib.ShellUtils

/**
 * Created by Mockingjay on 2017/8/22.
 */
class Shell{

    companion object {
        val TAG="TAG===Shell"
        fun HandleShell(comlist: ArrayList<String>,Root: Boolean): String{
            var result= ShellUtils.execCommand(comlist,Root,true)
            Log.d(TAG,"执行结果"+result.result.toString())
            Log.d(TAG,"成功结果"+result.successMsg )
            Log.d(TAG,"失败结果"+result.errorMsg )
            return result.successMsg

        }
        fun deleteThisHosts(before: String){
            var com= arrayListOf<String>()
            com.add("grep -n \\${before} /system/etc/hosts")
            var str=Shell.HandleShell(com,true)
            var one=str.indexOf(":#")
            var two=str.indexOf(":#",one+1)
            var bf=str.indexOf("before")
            var beforeline= str.substring(0,one)
            var laterline= str.substring(bf+6,two)
//            Log.d(TAG,beforeline)
//            Log.d(TAG,laterline)
            Shell.deleteLineToLine(beforeline,laterline)
        }
        fun deleteLineToLine(beforeNub: String,laterNub: String){
            var com= arrayListOf<String>()
            com.add("sed -i '${beforeNub},${laterNub}d'  /system/etc/hosts")
            Shell.HandleShell(com,true)

        }

    }

}