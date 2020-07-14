package com.example.leadreminder


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class CustomerListActivity : BaseActivity() {
    lateinit var adapter : CustomersAdapter
    lateinit var customers:ArrayList<Customer>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customerlist)
        customers = intent.extras?.get("customerList") as ArrayList<Customer>
//        customers.add(Customer(1,"yusuf","Mumbai"))
//        customers.add(Customer(2,"saif","Mumbai"))
//        customers.add(Customer(3,"marwadi","Mumbai"))
//        customers.add(Customer(4,"gujju","Mumbai"))
//        customers.add(Customer(6,"indraprasthwala","Mumbai"))
//        customers.add(Customer(7,"awdawd","Mumbai"))
//        customers.add(Customer(8,"dawdad","Mumbai"))
//        customers.add(Customer(9,"gfgfgfgf","Mumbai"))
//        customers.add(Customer(10,"indraprasthwala","Mumbai"))
//        customers.add(Customer(11,"indraprasthwala","Mumbai"))
//        customers.add(Customer(12,"indraprasthwala","Mumbai"))
//        customers.add(Customer(13,"indraprasthwala","Mumbai"))
//        customers.add(Customer(14,"indraprasthwala","Mumbai"))
//        customers.add(Customer(15,"indraprasthwala","Mumbai"))

        adapter = CustomersAdapter(customers)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val myActionMenuItem: MenuItem = menu!!.findItem(R.id.search)
        val searchView :androidx.appcompat.widget.SearchView= myActionMenuItem.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText)
                return true
            }
        })
        return true
    }

    override fun onResume() {
        super.onResume()
//        showProgressBar()
        AppExecutor.executor.execute {
            val updatedList = ArrayList<Customer>()
            for(customer in customers){
                val newCustomer = fetchCustomer(customer.appId)
                updatedList.add(newCustomer)
            }
            customers = updatedList
            AppExecutor.mainThread.post {
                adapter.setData(customers)
//                hideProgressBar()
            }
        }


    }
}