package com.example.leadreminder

import android.content.Context
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.*

class AppUtility {
    companion object{
        const val REFER_TO_COLLECTION = "REFER TO COLLECTION"
        const val SHOP_CLOSED = "SHOP CLOSED"
        const val LOGIN = "LOGIN"
        const val DISBURSEMENT = "DISBURSEMENT"
        const val SHOP_OPEN = "SHOP OPEN"
        const val REGULARIZED = "REGULARIZED"

        const val CUSTOMER: String = "customer"
        const val PREVIOUS_FILE: String = "PREVIOUS_FILE"
        const val NOTIFICATION_ID:String = "NOTIFICATION_ID"
        const val NOTIFICATION_NAME = "LeadReminder"
        const val NOTIFICATION_CHANNEL = "LeadReminder_channel_01"
        const val NOTIFICATION_WORK = "LeadReminder_notification_work"
        const val APP_ID = 0
        const val SHOP_NAME = 1
        const val MOBILE_NO = 2
        const val ADDRESS = 3
        const val STATUS = 4
        const val REASON = 5
        const val LASTCONTACTDATE = 6
        const val NEXTCONTACTDATE = 7
        val dateFormatter:SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val gson = GsonBuilder().registerTypeAdapter(Int::class.java,GsonIntAdapter()).disableHtmlEscaping().setDateFormat("dd/MM/yyyy").create()
        fun getCurrentDate(): Date {
            return Date(System.currentTimeMillis())
        }
        fun dateToString(date:Date): String {
            return dateFormatter.format(date)
        }
        fun getNotificationId(context: Context): Int {
            var id = 0
            id = PreferencesUtil.getInt(context, NOTIFICATION_ID,0)
            PreferencesUtil.putInt(context, NOTIFICATION_ID,id+1)
            return id
        }
    }
}