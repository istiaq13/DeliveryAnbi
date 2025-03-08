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
    var status: String = "Pending"
    var time: String = ""
    var userId: String = ""
    var acceptedBy: String = ""
    var location: String = ""

    constructor()

    constructor(
        orderId: String,
        foodName: String,
        quantity: Int,
        totalPrice: Double,
        status: String,
        time: String,
        userId: String,
        location: String,
        acceptedBy: String = ""
    ) {
        this.orderId = orderId
        this.foodName = foodName
        this.quantity = quantity
        this.totalPrice = totalPrice
        this.status = status
        this.time = time
        this.userId = userId
        this.location = location
        this.acceptedBy = acceptedBy
    }

    fun placeOrder() {
        val database = FirebaseDatabase.getInstance()
        val ordersRef = database.getReference("orders")
        val newOrderRef = ordersRef.push()
        this.orderId = newOrderRef.key ?: ""
        this.time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        newOrderRef.setValue(this)
    }
}

/*package com.example.dpproject.domain

import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Order {
    var orderId: String = ""
    var foodName: String = ""
    var quantity: Int = 0
    var totalPrice: Double = 0.0
    var status: String = "Pending"
    var time: String = ""
    var userId: String = ""
    var acceptedBy: String = ""
    var location: String = ""

    constructor()

    constructor(
        orderId: String,
        foodName: String,
        quantity: Int,
        totalPrice: Double,
        status: String,
        time: String,
        userId: String,
        location: String,
        acceptedBy: String = ""
    ) {
        this.orderId = orderId
        this.foodName = foodName
        this.quantity = quantity
        this.totalPrice = totalPrice
        this.status = status
        this.time = time
        this.userId = userId
        this.location = location
        this.acceptedBy = acceptedBy
    }

    fun placeOrder() {
        val database = FirebaseDatabase.getInstance()
        val ordersRef = database.getReference("orders")
        val newOrderRef = ordersRef.push()
        this.orderId = newOrderRef.key ?: ""
        this.time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        newOrderRef.setValue(this)
    }
}*/