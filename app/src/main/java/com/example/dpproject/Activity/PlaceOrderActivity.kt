package com.example.dpproject.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dpproject.R
import com.example.dpproject.domain.Order

class PlaceOrderActivity : AppCompatActivity() {

    private lateinit var foodNameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var totalPriceEditText: EditText
    private lateinit var placeOrderButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        foodNameEditText = findViewById(R.id.foodNameEditText)
        quantityEditText = findViewById(R.id.quantityEditText)
        totalPriceEditText = findViewById(R.id.totalPriceEditText)
        placeOrderButton = findViewById(R.id.placeOrderButton)

        placeOrderButton.setOnClickListener {
            val foodName = foodNameEditText.text.toString()
            val quantity = quantityEditText.text.toString().toInt()
            val totalPrice = totalPriceEditText.text.toString().toDouble()

            val order = Order("", foodName, quantity, totalPrice, "Pending")
            order.placeOrder()

            Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}