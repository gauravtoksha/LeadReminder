package com.example.leadreminder

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.util.*


class CustomerActivity : BaseActivity() {
    lateinit var customer:Customer
    lateinit var dateUpdateDialog: AlertDialog
    lateinit var datePickerDialog:DatePickerDialog
    val calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        datePickerDialog = DatePickerDialog(this,{ _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year,month,dayOfMonth)
            customer.nextContactDate = AppUtility.dateFormatter.format(calendar.time)
            findViewById<TextView>(R.id.nextContactDate).text = customer.nextContactDate
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))

        val list = ArrayList<String>()
        list.add(AppUtility.DISBURSEMENT)
        list.add(AppUtility.REGULARIZED)
        list.add(AppUtility.LOGIN)
        list.add(AppUtility.SHOP_CLOSED)
        list.add(AppUtility.REFER_TO_COLLECTION)
        list.add(AppUtility.SHOP_OPEN)
        val arrayAdapter = ArrayAdapter<String>(applicationContext,R.layout.spinner_item_text,list)
        val spinner = findViewById<Spinner>(R.id.status)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                customer.status = p0?.getItemAtPosition(p2).toString()
            }
        }
        findViewById<Button>(R.id.updateButton).setOnClickListener {
            customer.status = spinner.selectedItem.toString()
            updateSheet()
        }

        setCustomer()

        fetchAndUpdateViews()
    }

    private fun fetchAndUpdateViews() {
        showProgressBar()
        AppExecutor.executor.execute {
            customer = fetchCustomer(customer.appId)
            AppExecutor.mainThread.post {
                setCustomerViews()
                setListeners()
                hideProgressBar()
            }
        }
    }

    private fun setCustomer() {
        customer = if (intent.extras?.get(AppUtility.CUSTOMER) == null) {
            val data = Uri.parse(intent.data.toString())
            AppUtility.gson.fromJson(data.schemeSpecificPart, Customer::class.java)
        } else {
            intent.extras?.get(AppUtility.CUSTOMER) as Customer
        }
    }

    private fun setCustomerViews() {
        findViewById<TextView>(R.id.appId).text = customer.appId.toString()
        findViewById<TextView>(R.id.shopName).text = customer.shopName
        findViewById<TextView>(R.id.mobileNo).text = customer.mobileno.toString()
        selectSpinnerValue(findViewById(R.id.status), customer.status.toString())
        findViewById<TextView>(R.id.reason).text = customer.reason
        findViewById<TextView>(R.id.lastContactDate).text = customer.lastContactDate
        findViewById<TextView>(R.id.nextContactDate).text = customer.nextContactDate
        findViewById<TextView>(R.id.address).text = customer.address
    }
    private fun selectSpinnerValue(spinner: Spinner, myString: String) {
        val index = 0
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == myString) {
                spinner.setSelection(i)
                break
            }
        }
    }

    private fun setListeners() {
        findViewById<Button>(R.id.maps).setOnClickListener {
            if (this.customer.address != null && this.customer.address!!.isNotEmpty()) {
                val gmmIntentUri = Uri.parse("google.navigation:?q=${customer.address}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }
        findViewById<ImageButton>(R.id.phoneButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", customer.mobileno.toString(), null))
            startActivity(intent)
        }
        findViewById<ImageButton>(R.id.updateVisited).setOnClickListener {
            customer.lastContactDate = AppUtility.dateToString(AppUtility.getCurrentDate())
            findViewById<TextView>(R.id.lastContactDate).text = customer.lastContactDate
        }
        findViewById<ImageButton>(R.id.openCalender).setOnClickListener {
            datePickerDialog.show()
        }

    }

    private fun updateSheet() {
        showProgressBar()
        AppExecutor.executor.execute {
            updateCustomer(customer)
            AppExecutor.mainThread.post {
                hideProgressBar()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        setCustomer()
        fetchAndUpdateViews()
    }
}