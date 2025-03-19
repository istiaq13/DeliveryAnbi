package com.example.dpproject.utils

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

class OrderHistoryAnalyzer {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    fun getMostFrequentFood(callback: (String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val ordersRef = database.getReference("orders").orderByChild("userId").equalTo(userId)
        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val foodCountMap = mutableMapOf<String, Int>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let {
                        val foodName = it.foodName
                        foodCountMap[foodName] = foodCountMap.getOrDefault(foodName, 0) + 1
                    }
                }
                val mostFrequentFood = foodCountMap.maxByOrNull { it.value }?.key
                callback(mostFrequentFood)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun getLastOrderedFood(callback: (String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val ordersRef = database.getReference("orders").orderByChild("userId").equalTo(userId)
        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var lastOrder: Order? = null
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let {
                        if (lastOrder == null || it.time > lastOrder!!.time) {
                            lastOrder = it
                        }
                    }
                }
                callback(lastOrder?.foodName)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
}