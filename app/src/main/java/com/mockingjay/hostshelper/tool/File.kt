package com.mockingjay.hostshelper.tool

import android.content.Context
import android.text.TextUtils
import java.io.*
import java.io.File
import android.content.Intent
import android.net.Uri


/**
 * Created by Mockingjay on 2017/8/23.
 */
class File {
    companion object {
        fun getAppfiles(context: Context): String {
            return context.filesDir.absolutePath
        }

        /**
         * 以行为单位读取文件内容，一次读一整行，常用于读面向行的格式化文件,转换成Shell命令
         * @param filePath 文件路径
         */
        @Throws(IOException::class)
        fun readFileByLinesToListToShell(filePath: String,keyflag: String): ArrayList<String> {
            var reader: BufferedReader? = null
            var sb = arrayListOf<String>()
            sb.add("echo " +"" + " >> /etc/hosts")
            sb.add("mount -o rw,remount /system")
            sb.add("echo " +"\\"+ keyflag+"before" + " >> /etc/hosts")
            try {
                reader = BufferedReader(InputStreamReader(FileInputStream(filePath), System.getProperty("file.encoding")))
                var tempString: String? = reader.readLine()

                while (tempString != null) {
                    sb.add("echo " + tempString + " >> /etc/hosts")
                    tempString = reader.readLine()
                }
                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (reader != null) {
                    reader.close()
                }
            }
            sb.add("echo " +"\\"+  keyflag+"later" + " >> /etc/hosts")
            sb.add("chmod 644 /etc/hosts")
            return sb

        }

        /**
         * 写入应用程序包files目录下文件

         * @param context
         * *            上下文
         * *
         * @param fileName
         * *            文件名称
         * *
         * @param content
         * *            文件内容
         */
        fun writeIntofiles(context: Context, fileName: String, content: String) {
            try {

                val outStream = context.openFileOutput(fileName,
                        Context.MODE_PRIVATE)
                outStream.write(content.toByteArray())
                outStream.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun deleteFile(filePath: String) {
            if (TextUtils.isEmpty(filePath)) return
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }
        }

        @Throws(IOException::class)
        fun readFileByLines(filePath: String): String {
            var reader: BufferedReader? = null
            val sb = StringBuffer()
            try {
                reader = BufferedReader(InputStreamReader(FileInputStream(filePath), System.getProperty("file.encoding")))
                var tempString: String? = reader.readLine()
                while (tempString != null) {
                    sb.append(tempString)
                    sb.append("\n")
                    var tempString: String? = reader.readLine()
                }
                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (reader != null) {
                    reader.close()
                }
            }

            return sb.toString()

        }

        fun backSysHosts(appfilespath: String) {
            var com = arrayListOf<String>("mount -o rw,remount /system", "cp /system/etc/hosts " + appfilespath)
            Shell.HandleShell(com, true)

        }

        fun HostsToDefault(appfilespath: String) {
            var com = arrayListOf<String>("mount -o rw,remount /system", "cp " + appfilespath + "/hosts " + "/system/etc/")
            Shell.HandleShell(com, true)
        }

        fun readForString(strFilePath: String): String {
            val path = strFilePath
            var content = "" //文件内容字符串
            //打开文件
            val file = File(path)
            //如果path是传递过来的参数，可以做一个非目录的判断
            if (file.isDirectory()) {
                // Log.d("TestFile", "The File doesn't not exist.")
            } else {
                try {
                    val instream = FileInputStream(file)
                    if (instream != null) {
                        val inputreader = InputStreamReader(instream)
                        val buffreader = BufferedReader(inputreader)
                        var line: String = buffreader.readLine()
                        //分行读取
                        while (line != null) {
                            content += line + "\n"
                            line = buffreader.readLine()
                        }
                        instream.close()
                    }
                } catch (e: IOException) {
                    // Log.d("TestFile", "The File doesn't not exist.")
                } catch (e: IOException) {
                    //  Log.d("TestFile", e.getMessage())
                }

            }
            return content
        }

        /**
         * 保存内容
         * @param filePath 文件路径
         * *
         * @param content 保存的内容
         * *
         * @throws IOException
         */
        @Throws(IOException::class)
        fun saveToFile(filePath: String, content: String) {
            saveToFile(filePath, content, System.getProperty("file.encoding"))
        }

        /**
         * 指定编码保存内容
         * @param filePath 文件路径
         * *
         * @param content 保存的内容
         * *
         * @param encoding 写文件编码
         * *
         * @throws IOException
         */
        @Throws(IOException::class)
        fun saveToFile(filePath: String, content: String, encoding: String) {
            var writer: BufferedWriter? = null
            val file = File(filePath)
            try {
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file, false), encoding))
                writer.write(content)

            } finally {
                if (writer != null) {
                    writer.close()
                }
            }
        }

        fun getTextFileIntent(file: File): Intent {
            val intent = Intent("android.intent.action.VIEW")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = Uri.fromFile(file)
            intent.setDataAndType(uri, "text/plain")
            return intent
        }

    }
}