package com.example.dpproject.Activity

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dpproject.R
import com.example.dpproject.domain.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val orderId = intent.getStringExtra("orderId")
        if (orderId != null) {
            fetchOrderDetails(orderId)
        }
    }

    private fun fetchOrderDetails(orderId: String) {
        val database = FirebaseDatabase.getInstance()
        val orderRef = database.getReference("orders").child(orderId)
        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Order::class.java)
                order?.let { displayOrderDetails(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun displayOrderDetails(order: Order) {
        val titleTxt: TextView = findViewById(R.id.titleTxt)
        val locationTxt: TextView = findViewById(R.id.locationTxt)
        val quantityTxt: TextView = findViewById(R.id.quantityTxt)
        val priceTxt: TextView = findViewById(R.id.priceTxt)
        val rewardTxt: TextView = findViewById(R.id.rewardTxt)
        val timeTxt: TextView = findViewById(R.id.timeTxt)
        val acceptedByTxt: TextView = findViewById(R.id.acceptedByTxt)
        val orderedByTxt: TextView = findViewById(R.id.orderedByTxt)

        titleTxt.text = order.foodName
        locationTxt.text = order.location
        quantityTxt.text = "Quantity: ${order.quantity}"
        priceTxt.text = "BDT ${order.totalPrice}"
        rewardTxt.text = "Reward: ${order.totalPrice * 0.1} BDT"
        timeTxt.text = order.time

        // Fetch usernames for "Accepted By" and "Ordered By"
        fetchUsername(order.userId, orderedByTxt, "Ordered By: ")
        fetchUsername(order.acceptedBy, acceptedByTxt, "Accepted By: ")
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
}