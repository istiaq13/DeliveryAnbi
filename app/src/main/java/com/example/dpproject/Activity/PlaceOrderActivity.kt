package com.example.dpproject.Activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dpproject.R
import com.example.dpproject.domain.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class PlaceOrderActivity : AppCompatActivity() {
    private lateinit var foodNameEditText: EditText
    private lateinit var locationSpinner: Spinner
    private lateinit var quantityEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var additionalEditText: EditText
    private lateinit var paymentTextView: TextView
    private lateinit var placeOrderButton: Button
    private lateinit var cancelButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        foodNameEditText = findViewById(R.id.foodNameText)
        locationSpinner = findViewById(R.id.locationSpinner)
        quantityEditText = findViewById(R.id.quantityEditText)
        priceEditText = findViewById(R.id.priceEditText)
        additionalEditText = findViewById(R.id.additionalEditText)
        paymentTextView = findViewById(R.id.paymentTextView)
        placeOrderButton = findViewById(R.id.placeOrderButton)
        cancelButton = findViewById(R.id.cancelButton)

        val locations = arrayOf("Male Hall Of Residence", "Female Hall Of Residence")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locations)
        locationSpinner.adapter = adapter

        priceEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val price = s.toString().toDoubleOrNull() ?: 0.0
                val totalAmount = price + (price * 0.1)
                paymentTextView.text = "You Need To Pay ${totalAmount.toInt()} BDT For This Order"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cancelButton.setOnClickListener {
            finish()
        }

        placeOrderButton.setOnClickListener {
            val foodName = foodNameEditText.text.toString()
            val selectedLocation = locationSpinner.selectedItem.toString()
            val quantity = quantityEditText.text.toString().toIntOrNull() ?: 0
            val price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val additional = additionalEditText.text.toString()

            if (foodName.isEmpty() || selectedLocation == "Select Location" || quantity <= 0 || price <= 0) {
                Toast.makeText(this, "Please fill all required fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid ?: ""
            val order = Order(
                orderId = "",
                foodName = foodName,
                quantity = quantity,
                totalPrice = price,
                status = "Pending",
                time = "",
                userId = userId,
                location = selectedLocation
            )

            order.placeOrder()
            Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Auto-fill the form if a food name is passed via intent
        val foodName = intent.getStringExtra("foodName")
        foodName?.let {
            foodNameEditText.setText(it)
        }
    }
}