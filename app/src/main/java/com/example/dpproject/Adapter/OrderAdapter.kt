/*package com.example.dpproject.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dpproject.R
import com.example.dpproject.domain.Order

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.foodNameTextView.text = order.foodName
        holder.quantityTextView.text = "Quantity: ${order.quantity}"
        holder.totalPriceTextView.text = "Total: $${order.totalPrice}"
        holder.statusTextView.text = "Status: ${order.status}"
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodNameTextView: TextView = itemView.findViewById(R.id.foodNameTextView)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
        val totalPriceTextView: TextView = itemView.findViewById(R.id.totalPriceTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
    }
}*/