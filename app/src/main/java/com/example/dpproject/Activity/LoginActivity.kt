package com.example.dpproject.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dpproject.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Declare UI elements
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view before accessing any UI components
        setContentView(R.layout.activity_login)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Find UI elements
        emailText = findViewById(R.id.emailBox)
        passwordText = findViewById(R.id.passwordBox)
        val loginButton = findViewById<Button>(R.id.login_button)

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                showErrorDialog("Please fill all the fields")
            } else {
                // Call login function with email and password
                loginUser(email, password)
            }
        }

        // Handle signup page navigation
        val signup: TextView = findViewById(R.id.makeoneacc)
        signup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to log in the user using Firebase Authentication
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Login successful, navigate to dashboard
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish() // Close the login activity
                Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
            } else {
                // Login failed, show error popup dialog
                showErrorDialog("Login Failed. Check your email or password.")
            }
        }
    }

    // Function to show an error popup dialog
    private fun showErrorDialog(message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Error")
        alert.show()
    }
}
