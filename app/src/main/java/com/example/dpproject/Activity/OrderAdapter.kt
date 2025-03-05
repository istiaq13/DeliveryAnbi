package com.example.dpproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dpproject.R
import com.example.dpproject.domain.Order

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val orders = mutableListOf<Order>()

    fun submitList(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_best_deal, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTxt: TextView = itemView.findViewById(R.id.titleTxt)
        private val starTxt: TextView = itemView.findViewById(R.id.starTxt)
        private val priceTxt: TextView = itemView.findViewById(R.id.priceTxt)
        private val timeTxt: TextView = itemView.findViewById(R.id.timeTxt)

        fun bind(order: Order) {
            titleTxt.text = order.foodName
            starTxt.text = order.quantity.toString()
            priceTxt.text = "$${order.totalPrice}"
            timeTxt.text = order.time
        }
    }
}