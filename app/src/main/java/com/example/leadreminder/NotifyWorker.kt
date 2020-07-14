package com.example.leadreminder

import android.app.Notification.DEFAULT_ALL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Color.RED
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.leadreminder.AppUtility.Companion.NOTIFICATION_CHANNEL
import com.example.leadreminder.AppUtility.Companion.NOTIFICATION_NAME
import com.google.gson.GsonBuilder
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

class NotifyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    val customerList = ArrayList<Customer>()
    val gson = GsonBuilder().create()
    override fun doWork(): Result {
        customerList.addAll(fetchCustomersOnNextContactDate(AppUtility.dateToString(AppUtility.getCurrentDate())))
        var i=0
        while(customerList.isNotEmpty()){
            val id = AppUtility.getNotificationId(context = applicationContext)
            sendNotifications(customerList[i],id)
            customerList.removeAt(i)
        }
        return if(customerList.isEmpty()) Result.success() else Result.failure()
    }

    private fun sendNotifications(customer:Customer, id: Int) {
        val notificationManager =
        applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(applicationContext,CustomerActivity::class.java)
//            val intent = Intent().setClass(applicationContext,CustomerActivity::class.java)
        intent.action = id.toString()
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP)
        intent.data = Uri.fromParts("tel",gson.toJson(customer),null)
        val pendingIntent = getActivity(applicationContext,id,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(applicationContext,NOTIFICATION_CHANNEL).setSmallIcon(R.drawable.ic_notifications_active_24px).setContentTitle("Lead Reminder")
            .setContentText(customer.shopName)
            .setDefaults(DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        notification.priority = PRIORITY_MAX
        if (SDK_INT >= O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)

            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                .setContentType(CONTENT_TYPE_SONIFICATION).build()

            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, IMPORTANCE_HIGH)

            channel.enableLights(true)
            channel.lightColor = RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(id, notification.build())
    }


    private fun fetchCustomersOnNextContactDate(dateToString: String): ArrayList<Customer> {
        val dataFormatter = DataFormatter()
        val filePath = PreferencesUtil.getString(applicationContext,AppUtility.PREVIOUS_FILE)
        val uri = Uri.parse(filePath)
        val file = File(uri.path!!)
        val inputStream = FileInputStream(file.absolutePath)
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheet(workbook.getSheetName(0))
        val customerList = ArrayList<Customer>()
        for(row in sheet.rowIterator()){
            if(dataFormatter.formatCellValue(row.getCell(AppUtility.NEXTCONTACTDATE, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)) == dateToString){
                val appId:Int = try{
                    dataFormatter.formatCellValue(row.getCell(AppUtility.APP_ID,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).toInt()
                }catch (ex:NumberFormatException){
                    0
                }
                val shopName :String?= dataFormatter.formatCellValue(row.getCell(AppUtility.SHOP_NAME,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                val address:String? = dataFormatter.formatCellValue(row.getCell(AppUtility.ADDRESS,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                val lcd:String? = dataFormatter.formatCellValue(row.getCell(AppUtility.LASTCONTACTDATE,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                val mo:String? = dataFormatter.formatCellValue(row.getCell(AppUtility.MOBILE_NO,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                val ncd:String? = dataFormatter.formatCellValue(row.getCell(AppUtility.NEXTCONTACTDATE,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                val rea:String? = dataFormatter.formatCellValue(row.getCell(AppUtility.REASON,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                val sta:String? = dataFormatter.formatCellValue(row.getCell(AppUtility.STATUS,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                customerList.add(Customer(appId,shopName,address,lcd,ncd,sta,rea,mo))
            }
        }
        inputStream.close()
        return customerList
    }
}