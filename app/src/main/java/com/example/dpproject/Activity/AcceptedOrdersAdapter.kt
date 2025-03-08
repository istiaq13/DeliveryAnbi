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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AcceptedOrdersAdapter : RecyclerView.Adapter<AcceptedOrdersAdapter.AcceptedOrderViewHolder>() {
    private val orders = mutableListOf<Order>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun submitList(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptedOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_order_details, parent, false)
        return AcceptedOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: AcceptedOrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    inner class AcceptedOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTxt: TextView = itemView.findViewById(R.id.titleTxt)
        private val priceTxt: TextView = itemView.findViewById(R.id.priceTxt)
        private val timeTxt: TextView = itemView.findViewById(R.id.timeTxt)
        private val acceptedByTxt: TextView = itemView.findViewById(R.id.acceptedByTxt)
        private val orderedByTxt: TextView = itemView.findViewById(R.id.orderedByTxt)
        private val deliveredButton: Button = itemView.findViewById(R.id.deliveredButton)
        private val receivedButton: Button = itemView.findViewById(R.id.receivedButton)

        fun bind(order: Order) {
            titleTxt.text = order.foodName
            priceTxt.text = "BDT ${order.totalPrice}"
            timeTxt.text = order.time

            // Fetch and display usernames
            val database = FirebaseDatabase.getInstance()

            // Fetch orderedBy username
            database.getReference("users").child(order.userId).child("username")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = snapshot.value.toString()
                        orderedByTxt.text = "Ordered By: $username"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })

            // Fetch acceptedBy username
            database.getReference("users").child(order.acceptedBy).child("username")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = snapshot.value.toString()
                        acceptedByTxt.text = "Accepted By: $username"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })

            // Handle button visibility
            if (order.acceptedBy == currentUserId) {
                deliveredButton.visibility = View.VISIBLE
                receivedButton.visibility = View.GONE
            } else if (order.userId == currentUserId) {
                deliveredButton.visibility = View.GONE
                receivedButton.visibility = View.VISIBLE
            }

            deliveredButton.setOnClickListener {
                receivedButton.visibility = View.VISIBLE
                deliveredButton.isEnabled = false
            }

            receivedButton.setOnClickListener {
                deliveredButton.isEnabled = false
                receivedButton.isEnabled = false
            }
        }
    }
}