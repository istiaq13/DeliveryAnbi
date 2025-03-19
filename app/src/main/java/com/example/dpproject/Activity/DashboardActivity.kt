package com.example.dpproject.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
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
import com.example.dpproject.adapter.OrderDetailsAdapter
import com.example.dpproject.domain.Order
import com.example.dpproject.utils.OrderHistoryAnalyzer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orderDetailsAdapter: OrderDetailsAdapter
    private lateinit var orderHistoryAnalyzer: OrderHistoryAnalyzer
    private val handler = Handler()
    private val delay = 5 * 60 * 1000L // 5 minutes in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        orderHistoryAnalyzer = OrderHistoryAnalyzer()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView for pending orders
        orderRecyclerView = findViewById(R.id.orderlistView)
        orderRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        orderAdapter = OrderAdapter()
        orderRecyclerView.adapter = orderAdapter

        // Initialize RecyclerView for accepted orders
        categoryRecyclerView = findViewById(R.id.categoryview)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        orderDetailsAdapter = OrderDetailsAdapter()
        categoryRecyclerView.adapter = orderDetailsAdapter

        // Fetch and display user name
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

        // Fetch and display orders
        fetchOrders()

        // Handle refresh button click
        val refreshButton: TextView = findViewById(R.id.textView11)
        refreshButton.setOnClickListener {
            fetchOrders()
            Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show()
        }

        // Handle logout button click
        val logoutButton: ImageView = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            logoutUser()
        }

        // Handle filter_button click to open UserProfileActivity
        val filterButton: TextView = findViewById(R.id.filter_button)
        filterButton.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        // Handle placeOrderButton click
        val placeOrderButton: Button = findViewById(R.id.makeOrderButton)
        placeOrderButton.setOnClickListener {
            val intent = Intent(this, PlaceOrderActivity::class.java)
            startActivity(intent)
        }

        // Show food recommendation dialog
        showFoodRecommendationDialog()
    }

    private fun showFoodRecommendationDialog() {
        orderHistoryAnalyzer.getMostFrequentFood { mostFrequentFood ->
            orderHistoryAnalyzer.getLastOrderedFood { lastOrderedFood ->
                val recommendedFood = mostFrequentFood ?: lastOrderedFood
                recommendedFood?.let { foodName ->
                    val dialogView = layoutInflater.inflate(R.layout.dialog_food_recommendation, null)
                    val dialog = AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setCancelable(false)
                        .create()

                    val messageTextView = dialogView.findViewById<TextView>(R.id.messageTextView)
                    messageTextView.text = "Want to Order the $foodName Again?"

                    val askMeLaterButton = dialogView.findViewById<Button>(R.id.askMeLaterButton)
                    askMeLaterButton.setOnClickListener {
                        dialog.dismiss()
                    }

                    val orderAgainButton = dialogView.findViewById<Button>(R.id.orderAgainButton)
                    orderAgainButton.setOnClickListener {
                        val intent = Intent(this, PlaceOrderActivity::class.java)
                        intent.putExtra("foodName", foodName)
                        startActivity(intent)
                        dialog.dismiss()
                    }

                    val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
                    cancelButton.setOnClickListener {
                        dialog.dismiss()
                        handler.postDelayed({
                            showFoodRecommendationDialog()
                        }, delay)
                    }

                    dialog.show()
                }
            }
        }
    }

    private fun fetchOrders() {
        val ordersRef = database.getReference("orders")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                val acceptedOrders = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let {
                        if (it.acceptedBy.isNotEmpty()) {
                            acceptedOrders.add(it)
                        } else {
                            orders.add(it)
                        }
                    }
                }
                // Update adapters
                orderAdapter.submitList(orders.reversed())
                orderDetailsAdapter.submitList(acceptedOrders.reversed())
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

    fun onWalletButtonClick(view: View) {
        val intent = Intent(this, WalletActivity::class.java)
        startActivity(intent)
    }
}