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
        private val timeTxt: TextView = itemView.findViewById(R.id.timeTxt)
        private val acceptButton: Button = itemView.findViewById(R.id.acceptButton)
        private val cancelButton: Button = itemView.findViewById(R.id.cancelButton)
        private val acceptedSticker: TextView = itemView.findViewById(R.id.acceptedSticker)

        fun bind(order: Order) {
            titleTxt.text = order.foodName
            locationTxt.text = order.location
            quantityTxt.text = "Quantity: ${order.quantity}"
            priceTxt.text = "BDT ${order.totalPrice}"
            rewardTxt.text = "Reward: ${order.totalPrice * 0.1} BDT"
            timeTxt.text = order.time

            // Handle button visibility
            if (order.userId == currentUserId) {
                // Order created by the current user
                acceptButton.visibility = View.GONE
                cancelButton.visibility = View.VISIBLE
                acceptedSticker.visibility = View.GONE
            } else if (order.acceptedBy.isNotEmpty()) {
                // Order accepted by someone else
                acceptButton.visibility = View.GONE
                cancelButton.visibility = View.GONE
                acceptedSticker.visibility = View.VISIBLE
            } else {
                // Order is pending and can be accepted
                acceptButton.visibility = View.VISIBLE
                cancelButton.visibility = View.GONE
                acceptedSticker.visibility = View.GONE
            }

            // Handle accept button click
            acceptButton.setOnClickListener {
                acceptOrder(order.orderId)
            }

            // Handle cancel button click
            cancelButton.setOnClickListener {
                if (order.userId == currentUserId) {
                    // Main user cancels the order
                    deleteOrder(order.orderId)
                } else {
                    // Delivery man cancels the order
                    cancelOrder(order.orderId)
                }
            }
        }

        private fun acceptOrder(orderId: String) {
            val database = FirebaseDatabase.getInstance()
            val orderRef = database.getReference("orders").child(orderId)
            orderRef.child("acceptedBy").setValue(currentUserId)
            orderRef.child("status").setValue("Accepted")
        }

        private fun cancelOrder(orderId: String) {
            val database = FirebaseDatabase.getInstance()
            val orderRef = database.getReference("orders").child(orderId)
            orderRef.child("acceptedBy").setValue("")
            orderRef.child("status").setValue("Pending")
        }

        private fun deleteOrder(orderId: String) {
            val database = FirebaseDatabase.getInstance()
            val orderRef = database.getReference("orders").child(orderId)
            orderRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Remove the order from the adapter's list
                    orders.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
            }
        }
    }
}