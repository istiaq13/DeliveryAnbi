package com.example.dpproject.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dpproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WalletActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var balanceTextView: TextView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wallet) // Ensure you have a layout file named `activity_wallet.xml`
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize views
        balanceTextView = findViewById(R.id.balanceTextView) // Ensure this ID exists in your layout
        backButton = findViewById(R.id.backButton) // Ensure this ID exists in your layout

        // Fetch and display wallet balance
        fetchWalletBalance()

        // Handle back button click
        backButton.setOnClickListener {
            finish() // Close the activity and return to the previous screen
        }
    }

    private fun fetchWalletBalance() {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val userRef = database.getReference("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Assuming the wallet balance is stored under a "balance" field in the user's data
                        val balance = snapshot.child("balance").getValue(Double::class.java) ?: 0.0
                        balanceTextView.text = "Balance: BDT $balance"
                    } else {
                        Toast.makeText(this@WalletActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@WalletActivity, "Failed to fetch wallet balance", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}