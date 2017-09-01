package tool
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.EditText

/**
 * Created by Mockingjay on 2017/8/9.
 */
class AlertDialog{
companion object {
    fun <R>alertshure(context: Context,content: String,post: ()->R){
        var builder = AlertDialog.Builder(context)
        builder.setMessage(content)
        builder.setTitle("提示")
        builder.setPositiveButton("确认",DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            post()
        })
        builder.setNegativeButton("取消",null)
        builder.create().show()
    }
    fun <R>alertshuerview(context: Context,content: String,view: View,post: (String)->R,restore: ()->R){
        var builder = AlertDialog.Builder(context)
        builder.setMessage(content)
        builder.setTitle("提示")
        builder.setView(view)
        builder.setPositiveButton("确认",DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            var str=(view as EditText).text.toString()
            post(str)
        })
        builder.setNeutralButton("恢复默认",DialogInterface.OnClickListener{ dialog, which ->
            dialog.dismiss()
            restore()

        })
        builder.setNegativeButton("取消",null)
        builder.create().show()

    }
}

}
