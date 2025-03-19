package com.example.dpproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
        private val cancelButton: Button = itemView.findViewById(R.id.cancelButton)

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
                // User who accepted the order
                deliveredButton.visibility = View.VISIBLE
                receivedButton.visibility = View.GONE
                cancelButton.visibility = View.VISIBLE
            } else if (order.userId == currentUserId) {
                // User who created the order
                deliveredButton.visibility = View.GONE
                receivedButton.visibility = View.VISIBLE
                cancelButton.visibility = View.GONE
            }

            // Handle delivered button click
            deliveredButton.setOnClickListener {
                updateOrderStatus(order.orderId, "Delivered")
                deliveredButton.isEnabled = false
            }

            // Handle received button click
            receivedButton.setOnClickListener {
                // Remove the order from active orders
                removeOrderFromActiveOrders(order.orderId)

                // Send a message to the delivery man
                sendMessageToDeliveryMan(order.acceptedBy, "Order ${order.orderId} has been completed.")

                // Disable the button to prevent multiple clicks
                receivedButton.isEnabled = false
            }

            // Handle cancel button click
            cancelButton.setOnClickListener {
                // Move the order back to "all orders" list
                cancelOrder(order.orderId)
            }
        }

        private fun removeOrderFromActiveOrders(orderId: String) {
            val database = FirebaseDatabase.getInstance()
            val orderRef = database.getReference("orders").child(orderId)
            orderRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Remove the order from the adapter's list
                    orders.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    Toast.makeText(itemView.context, "Order removed successfully.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(itemView.context, "Failed to remove order: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun sendMessageToDeliveryMan(deliveryManId: String, message: String) {
            val database = FirebaseDatabase.getInstance()
            val messagesRef = database.getReference("messages").child(deliveryManId)
            val messageData = mapOf(
                "orderId" to "N/A", // Replace with actual order ID if needed
                "message" to message,
                "timestamp" to System.currentTimeMillis()
            )
            messagesRef.push().setValue(messageData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(itemView.context, "Message sent to delivery man.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(itemView.context, "Failed to send message: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun updateOrderStatus(orderId: String, status: String) {
            val database = FirebaseDatabase.getInstance()
            val orderRef = database.getReference("orders").child(orderId)
            orderRef.child("status").setValue(status).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(itemView.context, "Order status updated to $status.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(itemView.context, "Failed to update order status: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun cancelOrder(orderId: String) {
            val database = FirebaseDatabase.getInstance()
            val orderRef = database.getReference("orders").child(orderId)
            orderRef.child("acceptedBy").setValue("")
            orderRef.child("status").setValue("Pending").addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Remove the order from the adapter's list
                    orders.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    Toast.makeText(itemView.context, "Order canceled and moved back to all orders.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(itemView.context, "Failed to cancel order: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}