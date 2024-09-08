
package com.example.dpproject.Activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dpproject.R
import com.example.dpproject.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var showPasswordCheckBox: CheckBox
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        emailText = findViewById(R.id.emailBox)
        passwordText = findViewById(R.id.passwordBox)
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox)
        val loginButton = findViewById<Button>(R.id.login_button)

        // Show/hide password functionality
        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                passwordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Move cursor to the end of the text
            passwordText.setSelection(passwordText.text.length)
        }

        loginButton.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()

            if (!NetworkUtils.isNetworkConnected(this)) {
                showErrorDialog("Please check your internet connection and try again.")
            } else if (email.isBlank() || password.isBlank()) {
                showToast("Please fill all the fields")
            } else {
                loginUser(email, password)
            }
        }

        val signup: TextView = findViewById(R.id.makeoneacc)
        signup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Forgot password functionality
        val forgotPassword: TextView = findViewById(R.id.textView5)
        forgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun showForgotPasswordDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_forgot_password, null)
        dialogBuilder.setView(dialogView)

        val emailEditText = dialogView.findViewById<EditText>(R.id.emailEditText)
        val sendButton = dialogView.findViewById<Button>(R.id.sendButton)

        dialogBuilder.setTitle("Forgot Password")
        val alertDialog = dialogBuilder.create()

        sendButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isBlank()) {
                showToast("Please enter your email.")
            } else {
                sendPasswordByEmail(email)
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Redirect to DashboardActivity after successful login
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish() // Optionally, close the LoginActivity
            } else {
                showToast("Login Failed. Check your email or password.")
            }
        }
    }

    private fun sendPasswordByEmail(email: String) {
        val userRef = database.child("users").child(email.replace(".", ","))
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val password = snapshot.child("password").getValue(String::class.java)
                val userEmail = snapshot.child("email").getValue(String::class.java)

                // Send email using an intent
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.type = "message/rfc822"
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Password Recovery")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Here are your credentials:\nEmail: $userEmail\nPassword: $password")
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."))
                    showToast("Mail sent successfully.")
                } catch (e: Exception) {
                    showToast("Failed to send email.")
                }
            } else {
                showToast("User not found.")
            }
        }.addOnFailureListener { exception ->
            // Log the exception message
            showToast("Error retrieving user data: ${exception.message}")
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
