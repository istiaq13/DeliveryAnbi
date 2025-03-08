package com.example.dpproject.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dpproject.R
import com.example.dpproject.UserProfileActivity
import com.example.dpproject.adapter.OrderAdapter
import com.example.dpproject.domain.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        orderRecyclerView = findViewById(R.id.orderlistView)
        orderRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        orderAdapter = OrderAdapter()
        orderRecyclerView.adapter = orderAdapter

        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val userRef = database.getReference("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("username").value.toString()
                    val userNameTextView: TextView = findViewById(R.id.textView9)
                    userNameTextView.text = username

                    val filterButton: TextView = findViewById(R.id.filter_button)
                    if (username.isNotEmpty()) {
                        filterButton.text = username[0].toString().uppercase()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        fetchOrders()

        val logoutButton: ImageView = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            logoutUser()
        }

        val filterButton: TextView = findViewById(R.id.filter_button)
        filterButton.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        val placeOrderButton: Button = findViewById(R.id.makeOrderButton)
        placeOrderButton.setOnClickListener {
            val intent = Intent(this, PlaceOrderActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchOrders() {
        val ordersRef = database.getReference("orders")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let { orders.add(it) }
                }
                orderAdapter.submitList(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, "Failed to fetch orders", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}