package com.example.dpproject.adapter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.dpproject.R
import com.example.dpproject.Activity.ChatActivity
import com.example.dpproject.domain.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderDetailsAdapter : RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {
    private val orders = mutableListOf<Order>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun submitList(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_order_details, parent, false)
        return OrderDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTxt: TextView = itemView.findViewById(R.id.titleTxt)
        private val locationTxt: TextView = itemView.findViewById(R.id.locationTxt)
        private val quantityTxt: TextView = itemView.findViewById(R.id.quantityTxt)
        private val priceTxt: TextView = itemView.findViewById(R.id.priceTxt)
        private val rewardTxt: TextView = itemView.findViewById(R.id.rewardTxt)
        private val timeTxt: TextView = itemView.findViewById(R.id.timeTxt)
        private val acceptedByTxt: TextView = itemView.findViewById(R.id.acceptedByTxt)
        private val orderedByTxt: TextView = itemView.findViewById(R.id.orderedByTxt)
        private val deliveredButton: Button = itemView.findViewById(R.id.deliveredButton)
        private val receivedButton: Button = itemView.findViewById(R.id.receivedButton)
        private val chatButton: Button = itemView.findViewById(R.id.chatButton)
        private val cancelOrderButton: Button = itemView.findViewById(R.id.cancelOrderButton)

        fun bind(order: Order) {
            titleTxt.text = order.foodName
            locationTxt.text = order.location
            quantityTxt.text = "Quantity: ${order.quantity}"
            priceTxt.text = "BDT ${order.totalPrice}"
            rewardTxt.text = "Reward: ${order.totalPrice * 0.1} BDT"
            timeTxt.text = order.time

            // Fetch usernames for "Accepted By" and "Ordered By"
            fetchUsername(order.userId, orderedByTxt, "Ordered By: ")
            fetchUsername(order.acceptedBy, acceptedByTxt, "Accepted By: ")

            // Handle button visibility
            if (order.acceptedBy == currentUserId) {
                // User who accepted the order (delivery person)
                deliveredButton.visibility = View.VISIBLE
                receivedButton.visibility = View.GONE
                cancelOrderButton.visibility = View.VISIBLE
            } else if (order.userId == currentUserId) {
                // User who created the order
                deliveredButton.visibility = View.GONE
                receivedButton.visibility = View.GONE
                cancelOrderButton.visibility = View.VISIBLE
            } else {
                // Other users shouldn't see these buttons
                deliveredButton.visibility = View.GONE
                receivedButton.visibility = View.GONE
                cancelOrderButton.visibility = View.GONE
            }

            // Handle delivered button click
            deliveredButton.setOnClickListener {
                receivedButton.visibility = View.VISIBLE
                deliveredButton.isEnabled = false
                updateOrderStatus(order.orderId, "Delivered")
            }

            // Handle received button click
            receivedButton.setOnClickListener {
                showDeleteConfirmationDialog(order.orderId)
            }

            // Handle chat button click
            chatButton.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("chatId", order.orderId) // Use orderId as chatId
                intent.putExtra("receiverId", if (currentUserId == order.userId) order.acceptedBy else order.userId)
                itemView.context.startActivity(intent)
            }

            // Handle cancel button click with different behavior based on user role
            cancelOrderButton.setOnClickListener {
                showCancelConfirmationDialog(order)
            }

            // Disable buttons if order is completed
            if (order.status == "Completed") {
                deliveredButton.isEnabled = false
                receivedButton.isEnabled = false
                cancelOrderButton.isEnabled = false
            }
        }

        private fun fetchUsername(userId: String, textView: TextView, prefix: String) {
            if (userId.isNotEmpty()) {
                val database = FirebaseDatabase.getInstance()
                val userRef = database.getReference("users").child(userId)
                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = snapshot.child("username").value.toString()
                        textView.text = "$prefix$username"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        textView.text = "$prefix Unknown"
                    }
                })
            } else {
                textView.text = "$prefix Unknown"
            }
        }

        private fun updateOrderStatus(orderId: String, status: String) {
            val database = FirebaseDatabase.getInstance()
            val orderRef = database.getReference("orders").child(orderId)
            orderRef.child("status").setValue(status)
            Toast.makeText(itemView.context, "Order status updated to $status", Toast.LENGTH_SHORT).show()
        }

        private fun showDeleteConfirmationDialog(orderId: String) {
            val alertDialog = AlertDialog.Builder(itemView.context)
                .setTitle("Order Completed")
                .setMessage("Do you want to delete this from your feed?")
                .setPositiveButton("Yes") { dialog, _ ->
                    deleteOrder(orderId)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()
        }

        private fun showCancelConfirmationDialog(order: Order) {
            val message = if (order.userId == currentUserId) {
                "Are you sure you want to cancel this order? It will be deleted permanently."
            } else {
                "Are you sure you want to cancel this delivery? The order will be returned to the orders list."
            }

            val alertDialog = AlertDialog.Builder(itemView.context)
                .setTitle("Cancel Order")
                .setMessage(message)
                .setPositiveButton("Yes") { dialog, _ ->
                    if (order.userId == currentUserId) {
                        // Original user cancels, delete the order permanently
                        deleteOrder(order.orderId)
                        Toast.makeText(itemView.context, "Order cancelled and removed", Toast.LENGTH_SHORT).show()
                    } else {
                        // Delivery person cancels, reset the order to pending
                        resetOrderToPending(order.orderId)
                        Toast.makeText(itemView.context, "Order returned to pending state", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()
        }

        private fun deleteOrder(orderId: String) {
            val database = FirebaseDatabase.getInstance()
            val orderRef = database.getReference("orders").child(orderId)
            orderRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Remove the order from the adapter's list
                    val position = orders.indexOfFirst { it.orderId == orderId }
                    if (position != -1) {
                        orders.removeAt(position)
                        notifyItemRemoved(position)
                    }
                } else {
                    Toast.makeText(itemView.context, "Failed to delete order: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun resetOrderToPending(orderId: String) {
            val database = FirebaseDatabase.getInstance()
            val orderRef = database.getReference("orders").child(orderId)

            // Update the order status and clear the acceptedBy field
            val updates = hashMapOf<String, Any>(
                "status" to "Pending",
                "acceptedBy" to ""
            )

            orderRef.updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Remove the order from the adapter's list
                    val position = orders.indexOfFirst { it.orderId == orderId }
                    if (position != -1) {
                        orders.removeAt(position)
                        notifyItemRemoved(position)
                    }
                } else {
                    Toast.makeText(itemView.context, "Failed to reset order: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}