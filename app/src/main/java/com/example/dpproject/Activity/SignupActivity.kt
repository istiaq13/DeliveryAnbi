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
import com.example.dpproject.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class SignupActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var signUpButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Firebase Realtime Database with your custom URL
        database = FirebaseDatabase.getInstance("https://dp-project-c2647-default-rtdb.firebaseio.com/").reference

        signUpButton = findViewById(R.id.signup_button)
        nameEditText = findViewById(R.id.nameBox)
        emailEditText = findViewById(R.id.emailBox)
        passwordEditText = findViewById(R.id.passwordBox)

        signUpButton.setOnClickListener {
            username = nameEditText.text.toString().trim()
            password = passwordEditText.text.toString().trim()
            email = emailEditText.text.toString().trim()

            if (!NetworkUtils.isNetworkConnected(this)) {
                Toast.makeText(this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show()
            } else if (username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please Fill All The Fields", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Send email verification and notify user
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            Toast.makeText(this, "Please verify your email and log in", Toast.LENGTH_SHORT).show()
                            saveUserData()
                            initializeOrdersSegment()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Error sending verification email: ${verificationTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    // Show specific error message for the failed signup
                    Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserData() {
        username = nameEditText.text.toString().trim()
        email = emailEditText.text.toString().trim()
        password = passwordEditText.text.toString().trim()

        // Hash the password before saving
        val hashedPassword = hashPassword(password)

        // Create a UserModel object
        val user = UserModel(username, email, hashedPassword)
        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        // Save user data in Firebase Realtime Database under "users" segment
        database.child("users").child(userID).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show()
                    Log.d("SignupActivity", "User data saved to Firebase Realtime Database")
                } else {
                    Toast.makeText(this, "Failed to save user data: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    Log.e("SignupActivity", "Failed to save user data: ${task.exception?.message}")
                }
            }
    }

    private fun initializeOrdersSegment() {
        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        // Initialize the "orders" segment for the user
        val ordersRef = database.child("orders").child(userID)

        // Create sub-sections for Generated Orders, Received Orders, and Cancelled Orders
        ordersRef.child("Generated Orders").setValue(hashMapOf<String, Any>())
        ordersRef.child("Received Orders").setValue(hashMapOf<String, Any>())
        ordersRef.child("Cancelled Orders").setValue(hashMapOf<String, Any>())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Orders segment initialized successfully", Toast.LENGTH_SHORT).show()
                    Log.d("SignupActivity", "Orders segment initialized in Firebase Realtime Database")
                } else {
                    Toast.makeText(this, "Failed to initialize orders segment: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    Log.e("SignupActivity", "Failed to initialize orders segment: ${task.exception?.message}")
                }
            }
    }

    // Function to hash the password using SHA-256
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}