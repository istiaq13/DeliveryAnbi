package com.example.dpproject.Activity

import android.os.Bundle
import android.widget.Button
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
    private lateinit var addMoneyButton: Button
    private lateinit var withdrawButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wallet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize views
        balanceTextView = findViewById(R.id.balanceTextView)
        addMoneyButton = findViewById(R.id.addMoneyButton)
        withdrawButton = findViewById(R.id.withdrawButton)

        // Fetch and display wallet balance
        fetchWalletBalance()

        // Handle add money button click
        addMoneyButton.setOnClickListener {
            // Open a dialog or new activity to add money
            showAddMoneyDialog()
        }

        // Handle withdraw button click
        withdrawButton.setOnClickListener {
            // Open a dialog or new activity to withdraw money
            showWithdrawDialog()
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
                        val balance = snapshot.child("balance").getValue(Double::class.java) ?: 0.0
                        balanceTextView.text = "Balance: BDT $balance"
                    } else {
                        balanceTextView.text = "Balance: BDT 0.0"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    balanceTextView.text = "Failed to fetch balance"
                    Toast.makeText(this@WalletActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showAddMoneyDialog() {
        // Implement a dialog to add money
        val dialog = AddMoneyDialogFragment()
        dialog.show(supportFragmentManager, "AddMoneyDialog")
    }

    private fun showWithdrawDialog() {
        // Implement a dialog to withdraw money
        val dialog = WithdrawDialogFragment()
        dialog.show(supportFragmentManager, "WithdrawDialog")
    }

    fun addMoney(amount: Double) {
        if (amount <= 0) {
            Toast.makeText(this, "Invalid amount. Please enter a positive value.", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val userRef = database.getReference("users").child(userId)
            userRef.child("balance").get().addOnSuccessListener { snapshot ->
                val currentBalance = snapshot.getValue(Double::class.java) ?: 0.0
                val newBalance = currentBalance + amount
                userRef.child("balance").setValue(newBalance).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        fetchWalletBalance() // Refresh the balance
                        Toast.makeText(this, "BDT $amount added successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to add money: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching balance: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    fun withdrawMoney(amount: Double) {
        if (amount <= 0) {
            Toast.makeText(this, "Invalid amount. Please enter a positive value.", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val userRef = database.getReference("users").child(userId)
            userRef.child("balance").get().addOnSuccessListener { snapshot ->
                val currentBalance = snapshot.getValue(Double::class.java) ?: 0.0
                if (currentBalance >= amount) {
                    val newBalance = currentBalance - amount
                    userRef.child("balance").setValue(newBalance).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            fetchWalletBalance() // Refresh the balance
                            Toast.makeText(this, "BDT $amount withdrawn successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to withdraw money: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Insufficient balance.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching balance: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}