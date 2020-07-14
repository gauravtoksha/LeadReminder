package com.example.leadreminder

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


open class BaseActivity: AppCompatActivity() {
    lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.setProperty("javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
    }
    fun showProgressBar(){
        dialog.show()
    }
    fun hideProgressBar(){
        dialog.hide()
    }
    fun updateCustomer(customer: Customer){
        val dataFormatter = DataFormatter()
        val filePath = PreferencesUtil.getString(this,AppUtility.PREVIOUS_FILE)
        val uri = Uri.parse(filePath)
        val file = File(uri.path!!)
        val inputStream = FileInputStream(file.absolutePath)
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheet(workbook.getSheetName(0))
//        val row = sheet.getRow(AppUtility.rowLookUp[customer.appId]!!)
        for(row in sheet.rowIterator()){
            if(dataFormatter.formatCellValue(row.getCell(AppUtility.APP_ID)) == customer.appId.toString()){
                row.getCell(AppUtility.SHOP_NAME, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(customer.shopName)
                row.getCell(AppUtility.ADDRESS,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(customer.address)
                row.getCell(AppUtility.LASTCONTACTDATE,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(customer.lastContactDate)
                row.getCell(AppUtility.MOBILE_NO,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(customer.mobileno)
                row.getCell(AppUtility.NEXTCONTACTDATE,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(customer.nextContactDate)
                row.getCell(AppUtility.REASON,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(customer.reason)
                row.getCell(AppUtility.STATUS,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(customer.status)
                inputStream.close()
                val outputStream = FileOutputStream(file.absolutePath)
                workbook.write(outputStream)
                outputStream.close()
            }
        }
    }
    fun fetchCustomer(appId:Int): Customer {
        val dataFormatter = DataFormatter()
        val filePath = PreferencesUtil.getString(this,AppUtility.PREVIOUS_FILE)
        val uri = Uri.parse(filePath)
        val file = File(uri.path!!)
        val inputStream = FileInputStream(file.absolutePath)
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheet(workbook.getSheetName(0))
//        val row = sheet.getRow(AppUtility.rowLookUp[appId]!!)
        for(row in sheet.rowIterator()){
            if(dataFormatter.formatCellValue(row.getCell(AppUtility.APP_ID)) == appId.toString()){
                val shopName :String?= dataFormatter.formatCellValue(row.getCell(AppUtility.SHOP_NAME))
                val address:String? = dataFormatter.formatCellValue(row.getCell(AppUtility.ADDRESS))
                val lcd:String? = dataFormatter.formatCellValue(row.getCell(AppUtility.LASTCONTACTDATE))
                val mo:String? = dataFormatter.formatCellValue(row.getCell(AppUtility.MOBILE_NO))
                val ncd:String? = dataFormatter.formatCellValue(row.getCell(AppUtility.NEXTCONTACTDATE))
                val rea:String? = dataFormatter.formatCellValue(row.getCell(AppUtility.REASON))
                val sta:String? = dataFormatter.formatCellValue(row.getCell(AppUtility.STATUS))
                inputStream.close()
                return Customer(appId,shopName,address,lcd,ncd,sta,rea,mo)
            }
        }
        inputStream.close()
        return Customer(0,null,null,null,null,null,null,null)
    }

    override fun onPause() {
        super.onPause()
//        dialog.dismiss()
    }
}