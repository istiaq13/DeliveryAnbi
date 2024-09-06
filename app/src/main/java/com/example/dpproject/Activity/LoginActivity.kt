package com.example.dpproject.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dpproject.R
import com.example.dpproject.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        emailText = findViewById(R.id.emailBox)
        passwordText = findViewById(R.id.passwordBox)
        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()

            if (!NetworkUtils.isNetworkConnected(this)) {
                showErrorDialog("Please check your internet connection and try again.")
            } else if (email.isBlank() || password.isBlank()) {
                showErrorDialog("Please fill all the fields")
            } else {
                loginUser(email, password)
            }
        }

        val signup: TextView = findViewById(R.id.makeoneacc)
        signup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
            } else {
                showErrorDialog("Login Failed. Check your email or password.")
            }
        }
    }

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