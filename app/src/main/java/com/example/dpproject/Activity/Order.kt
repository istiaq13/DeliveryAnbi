package com.example.dpproject.domain

import com.google.firebase.database.FirebaseDatabase

class Order {
    var orderId: String = ""
    var foodName: String = ""
    var quantity: Int = 0
    var totalPrice: Double = 0.0
    var status: String = ""

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(Order::class.java)
    }

    constructor(orderId: String, foodName: String, quantity: Int, totalPrice: Double, status: String) {
        this.orderId = orderId
        this.foodName = foodName
        this.quantity = quantity
        this.totalPrice = totalPrice
        this.status = status
    }

    fun placeOrder() {
        val database = FirebaseDatabase.getInstance()
        val ordersRef = database.getReference("orders")
        val newOrderRef = ordersRef.push()
        this.orderId = newOrderRef.key ?: ""
        newOrderRef.setValue(this)
    }
}