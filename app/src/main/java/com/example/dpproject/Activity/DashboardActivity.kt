package com.example.dpproject.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dpproject.R
import com.example.dpproject.UserProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signup: TextView = findViewById(R.id.filter_button)
        signup.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        // Handle logout button click
        val logoutButton: ImageView = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            logoutUser()
        }

        // Fetch and display user name
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val userRef = database.getReference("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("username").value.toString()
                    val userNameTextView: TextView = findViewById(R.id.textView9)
                    userNameTextView.text = username
                }

                override fun onCancelled(error: DatabaseError) {
                    //Toast.makeText(this@DashboardActivity, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun logoutUser() {
        // Log out from Firebase
        auth.signOut()

        // Redirect to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear backstack
        startActivity(intent)
        finish() // Finish DashboardActivity
    }
}