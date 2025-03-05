package com.example.dpproject.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dpproject.R
import com.example.dpproject.domain.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaceOrderActivity : AppCompatActivity() {

    private lateinit var foodNameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var totalPriceEditText: EditText
    private lateinit var placeOrderButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        foodNameEditText = findViewById(R.id.foodNameEditText)
        quantityEditText = findViewById(R.id.quantityEditText)
        totalPriceEditText = findViewById(R.id.totalPriceEditText)
        placeOrderButton = findViewById(R.id.placeOrderButton)

        placeOrderButton.setOnClickListener {
            val foodName = foodNameEditText.text.toString()
            val quantity = quantityEditText.text.toString().toIntOrNull() ?: 0
            val totalPrice = totalPriceEditText.text.toString().toDoubleOrNull() ?: 0.0

            if (foodName.isNotEmpty() && quantity > 0 && totalPrice > 0) {
                val orderId = database.reference.child("orders").push().key
                val time = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                val order = Order(orderId = "", foodName, quantity, totalPrice, "Pending", time)

                // Save order to Firebase
                orderId?.let {
                    database.reference.child("orders").child(it).setValue(order)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }
}