package com.example.dpproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dpproject.R
import com.example.dpproject.domain.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private val orders = mutableListOf<Order>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTxt: TextView = itemView.findViewById(R.id.titleTxt)
        private val locationTxt: TextView = itemView.findViewById(R.id.locationTxt)
        private val quantityTxt: TextView = itemView.findViewById(R.id.quantityTxt)
        private val priceTxt: TextView = itemView.findViewById(R.id.priceTxt)
        private val rewardTxt: TextView = itemView.findViewById(R.id.rewardTxt)
        private val acceptButton: Button = itemView.findViewById(R.id.acceptButton)
        private val cancelButton: Button = itemView.findViewById(R.id.cancelButton)
        private val acceptedSticker: TextView = itemView.findViewById(R.id.acceptedSticker)

        fun bind(order: Order) {
            titleTxt.text = order.foodName
            locationTxt.text = order.location
            quantityTxt.text = "Quantity: ${order.quantity}"
            priceTxt.text = "BDT ${order.totalPrice}"
            rewardTxt.text = "Reward: ${order.totalPrice * 0.1} BDT"

            if (order.userId == currentUserId) {
                acceptButton.visibility = View.GONE
                cancelButton.visibility = View.VISIBLE
                acceptedSticker.visibility = View.GONE
            } else if (order.acceptedBy.isNotEmpty()) {
                acceptButton.visibility = View.GONE
                cancelButton.visibility = View.GONE
                acceptedSticker.visibility = View.VISIBLE
            } else {
                acceptButton.visibility = View.VISIBLE
                cancelButton.visibility = View.GONE
                acceptedSticker.visibility = View.GONE
            }

            acceptButton.setOnClickListener {
                val database = FirebaseDatabase.getInstance()
                val orderRef = database.getReference("orders").child(order.orderId)
                orderRef.child("acceptedBy").setValue(currentUserId)
                orderRef.child("status").setValue("Accepted")
            }

            cancelButton.setOnClickListener {
                val database = FirebaseDatabase.getInstance()
                val orderRef = database.getReference("orders").child(order.orderId)
                orderRef.removeValue()
            }
        }
    }
}