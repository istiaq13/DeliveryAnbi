package com.example.dpproject.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dpproject.R
import com.example.dpproject.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    // Declare UI elements without initializing them
    private lateinit var signUpButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view first
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Now initialize UI elements after setting the content view
        signUpButton = findViewById(R.id.signup_button)
        nameEditText = findViewById(R.id.nameBox)
        emailEditText = findViewById(R.id.emailBox)
        passwordEditText = findViewById(R.id.passwordBox)

        signUpButton.setOnClickListener {
            username = nameEditText.text.toString().trim()
            password = passwordEditText.text.toString().trim()
            email = emailEditText.text.toString().trim()

            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please Fill All The Fields", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }

        // Apply window insets after setting the content view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()

                saveUserData()
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                Log.d("Account", "createAccount: Failure", task.exception)
            }
        }
    }

    private fun saveUserData() {
        username = nameEditText.text.toString().trim()
        password = passwordEditText.text.toString().trim()
        email = emailEditText.text.toString().trim()
        val user = UserModel(username, email, password)
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("user").child(userID).setValue(user)
    }
}