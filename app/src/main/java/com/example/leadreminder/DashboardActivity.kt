package com.example.leadreminder

//import androidx.cardview.widget.CardView
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import soup.neumorphism.NeumorphCardView as CardView

class DashboardActivity : AppCompatActivity() {
    private lateinit var customerList:ArrayList<Customer>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        customerList = intent.extras?.get("customerList") as ArrayList<Customer>

        findViewById<CardView>(R.id.allLeadsCard).setOnClickListener {
            goToCustomerList(customerList)
        }
        findViewById<CardView>(R.id.disbursedCard).setOnClickListener {
            goToCustomerList(getFilteredList(AppUtility.DISBURSEMENT))
        }
        findViewById<CardView>(R.id.regularizedCard).setOnClickListener {
            goToCustomerList(getFilteredList(AppUtility.REGULARIZED))
        }
        findViewById<CardView>(R.id.shopCLosedCard).setOnClickListener {
            goToCustomerList(getFilteredList(AppUtility.SHOP_CLOSED))
        }
        findViewById<CardView>(R.id.loginCard).setOnClickListener {
            val list = ArrayList<String>()
            list.add(AppUtility.LOGIN)
            list.add(AppUtility.REGULARIZED)
            list.add(AppUtility.DISBURSEMENT)
            goToCustomerList(getFilteredList(list))
        }
        findViewById<CardView>(R.id.othersCard).setOnClickListener {
            val list = getOthersExceptFilters()
            goToCustomerList(getFilteredListNot(list))
        }

        AppExecutor.executor.execute {
            val all  = customerList.size
            val disbursed = getStatusCount(AppUtility.DISBURSEMENT)
            val regularized = getStatusCount(AppUtility.REGULARIZED)
            val shopclosed = getStatusCount(AppUtility.SHOP_CLOSED)
            val login = getStatusCount(AppUtility.LOGIN)+disbursed+regularized
            val others = getStatusCountExcept(getOthersExceptFilters())
            AppExecutor.mainThread.post {
                animateValues(0,all,findViewById(R.id.allLeadCount))
                animateValues(0,disbursed,findViewById(R.id.disbursementCount))
                animateValues(0,regularized,findViewById(R.id.regularizedCount))
                animateValues(0,shopclosed,findViewById(R.id.shopClosedCount))
                animateValues(0,login,findViewById(R.id.loginCount))
                animateValues(0,others,findViewById(R.id.othersCount))
            }
        }

    }

    private fun getFilteredList(status: java.util.ArrayList<String>): java.util.ArrayList<Customer> {
        val filtered = ArrayList<Customer>()
        for(customer in customerList){
            if(customer.status in status){
                filtered.add(customer)
            }
        }
        return filtered
    }

    fun animateValues(start:Int,end:Int,textView: TextView){
        val va = ValueAnimator.ofInt(start,end)
        va.duration = 2000
        va.addUpdateListener {
            textView.text = va.animatedValue.toString()
        }
        va.start()
    }

    private fun getOthersExceptFilters(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add(AppUtility.DISBURSEMENT)
        list.add(AppUtility.REGULARIZED)
        list.add(AppUtility.SHOP_CLOSED)
        list.add(AppUtility.LOGIN)
        return list
    }

    fun getStatusCountExcept(status:ArrayList<String>): Int {
        var count = 0
        for(customer in customerList){
            if(customer.status !in status){
                ++count
            }
        }
        return count
    }

    fun getStatusCount(status:String): Int {
        var count = 0
        for(customer in customerList){
            if(customer.status.equals(status)){
                ++count
            }
        }
        return count
    }

    fun getFilteredList(status:String): ArrayList<Customer> {
        val filtered = ArrayList<Customer>()
        for(customer in customerList){
            if(customer.status.equals(status)){
                filtered.add(customer)
            }
        }
        return filtered
    }
    fun getFilteredListNot(status:ArrayList<String>): ArrayList<Customer> {
        val filtered = ArrayList<Customer>()
        for(customer in customerList){
            if(customer.status !in status){
                filtered.add(customer)
            }
        }
        return filtered
    }
    fun goToCustomerList(customerList:ArrayList<Customer>){
        val intent = Intent(this,CustomerListActivity::class.java)
        intent.putExtra("customerList", customerList)
        startActivity(intent)
    }
}