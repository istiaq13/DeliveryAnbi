package com.example.dpproject.domain

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
}