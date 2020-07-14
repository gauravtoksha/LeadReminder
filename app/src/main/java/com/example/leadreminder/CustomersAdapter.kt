package com.example.leadreminder

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_customer_item.view.*
import java.io.Serializable

class CustomersAdapter() : RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder>() {
    var customerList:ArrayList<Customer> = ArrayList()
    var filterList:ArrayList<Customer> = ArrayList()

    constructor(customerList: ArrayList<Customer>) : this() {
        this.customerList.addAll(customerList)
        this.filterList.addAll(customerList)
    }
    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val child = LayoutInflater.from(parent.context).inflate(R.layout.layout_customer_item,parent,false)
        return CustomerViewHolder(child)
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = filterList[position]
        holder.itemView.customerId.text = customer.appId.toString()
        holder.itemView.customerCity.text = customer.address
        holder.itemView.customerName.text = customer.shopName
        holder.itemView.status.text = customer.status
        holder.itemView.customerDueDate.text = customer.nextContactDate
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,CustomerActivity::class.java)
            intent.putExtra("customer",customer as Serializable)
            ContextCompat.startActivity(holder.itemView.context,intent,null)
        }
    }

    fun filter(newText: String?) {
        filterList.clear()
        if(newText == null || newText == ""){
            filterList.addAll(customerList)
        }else{
            for (customer in customerList){
                if(customer.shopName.toString().contains(newText,true) || customer.appId.toString().contains(newText,true)){
                    filterList.add(customer)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun setData(customers: java.util.ArrayList<Customer>) {
        filterList.clear()
        customerList.clear()
        filterList.addAll(customers)
        customerList.addAll(customers)
        notifyDataSetChanged()
    }
}