package com.example.dpproject.domain

import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Order {
    var orderId: String = ""
    var foodName: String = ""
    var quantity: Int = 0
    var totalPrice: Double = 0.0
    var status: String = "Pending" // Default status
    var time: String = "" // Added time field

    // Default constructor required for Firebase
    constructor()

    // Parameterized constructor
    constructor(
        orderId: String,
        foodName: String,
        quantity: Int,
        totalPrice: Double,
        status: String,
        time: String
    ) {
        this.orderId = orderId
        this.foodName = foodName
        this.quantity = quantity
        this.totalPrice = totalPrice
        this.status = status
        this.time = time
    }

    // Function to place an order
    fun placeOrder() {
        val database = FirebaseDatabase.getInstance()
        val ordersRef = database.getReference("orders")

        // Generate a unique order ID
        val newOrderRef = ordersRef.push()
        this.orderId = newOrderRef.key ?: ""

        // Set the current time
        this.time = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        // Save the order to Firebase
        newOrderRef.setValue(this)
    }
}