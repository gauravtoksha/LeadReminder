package com.example.leadreminder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.aditya.filebrowser.Constants
import com.aditya.filebrowser.FileChooser
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity() {
    val PICK_FILE_REQUEST = 100
    lateinit var file: Uri
    var gson: Gson = AppUtility.gson
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if(!permissionGranted()){
            requestPermission()
        }

        findViewById<Button>(R.id.filepickbutton).setOnClickListener {
            val intent = Intent(applicationContext,FileChooser::class.java)
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal)
            startActivityForResult(intent,PICK_FILE_REQUEST)
        }
        findViewById<Button>(R.id.previousFileButton).setOnClickListener {
            val filePath:String? = PreferencesUtil.getString(applicationContext,AppUtility.PREVIOUS_FILE)
            if(filePath!=null){
                file = Uri.parse(filePath)
                showProgressBar()
                AppExecutor.executor.execute {
                    val result = processFile(file)
                    AppExecutor.mainThread.post {
                        hideProgressBar()
                        scheduleNotifications()
                        goToDashboard(result)
                    }
                }
            }
        }





    }

    private fun scheduleNotifications() {
        val work = PeriodicWorkRequestBuilder<NotifyWorker>(5,TimeUnit.HOURS).build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniquePeriodicWork("Notification",ExistingPeriodicWorkPolicy.KEEP,work)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_FILE_REQUEST && data!=null){
            if(resultCode == Activity.RESULT_OK){
                file = data.data!!
                PreferencesUtil.putString(applicationContext,AppUtility.PREVIOUS_FILE,file.path)
                AppExecutor.mainThread.post {
                    showProgressBar()
                }
                AppExecutor.executor.execute {
                    val result = processFile(file)
                    AppExecutor.mainThread.post {
                        hideProgressBar()
                        goToDashboard(result)
                    }
                }
            }
        }
    }

    private fun goToCustomerList(result: JsonArray?) {
        val type = object : TypeToken<List<Customer>>() {}.type
        val customerList: ArrayList<Customer> =
            gson.fromJson<List<Customer>>(result, type) as ArrayList<Customer>
        val intent = Intent(this, CustomerListActivity::class.java)
        intent.putExtra("customerList", customerList)
        startActivity(intent)
    }
    private fun goToDashboard(result: JsonArray?) {
        val type = object : TypeToken<List<Customer>>() {}.type
        val customerList: ArrayList<Customer> =
            gson.fromJson<List<Customer>>(result, type) as ArrayList<Customer>
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("customerList", customerList)
        startActivity(intent)
    }

    private fun processFile(file: Uri): JsonArray? {
        val excelFile:File = File(file.path!!)
        val jsonObject = getExcelDataAsJsonObject(excelFile)
        return jsonObject?.get(jsonObject.keySet().elementAt(0))?.asJsonArray
    }

    private fun getExcelDataAsJsonObject(excelFile: File?): JsonObject? {
        val dataFormatter = DataFormatter()
        val sheetsJsonObject = JsonObject()
        var workbook: Workbook? = null
            workbook = XSSFWorkbook(excelFile)
        for (i in 0 until workbook.getNumberOfSheets()) {
            val sheetArray = JsonArray()
            val columnNames = ArrayList<String>()
            val sheet: Sheet = workbook.getSheetAt(i)
            val sheetIterator: Iterator<Row> = sheet.iterator()
            while (sheetIterator.hasNext()) {
                val currentRow: Row = sheetIterator.next()
                val jsonObject = JsonObject()
                if (currentRow.rowNum != 0) {
                    if(dataFormatter.formatCellValue(currentRow.getCell(AppUtility.APP_ID,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)) == ""){
                        break
                    }
                    for (j in 0 until columnNames.size) {
                        if (currentRow.getCell(j) != null && currentRow.getCell(j).toString().isNotEmpty()) {
                            when {
                                currentRow.getCell(j).cellTypeEnum === CellType.STRING -> {
                                    jsonObject.addProperty(
                                        columnNames[j],
                                        currentRow.getCell(j).stringCellValue.trim()
                                    )
                                }
                                currentRow.getCell(j)
                                    .cellTypeEnum === CellType.NUMERIC
                                -> {
                                    jsonObject.addProperty(
                                        columnNames[j],
                                        currentRow.getCell(j).numericCellValue.toLong()
                                    )
                                }
                                currentRow.getCell(j)
                                    .cellTypeEnum === CellType.BOOLEAN
                                -> {
                                    jsonObject.addProperty(
                                        columnNames[j],
                                        currentRow.getCell(j).booleanCellValue
                                    )
                                }
                            }
                        }
                    }
                    sheetArray.add(jsonObject)
                } else {
                    // store column names
                    for (k in 0 until currentRow.physicalNumberOfCells) {
                        columnNames.add(currentRow.getCell(k).stringCellValue)
                    }
                }
            }
            sheetsJsonObject.add(workbook.getSheetName(i), sheetArray)
        }
        return sheetsJsonObject
    }

    private fun permissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            1
        )
    }
}