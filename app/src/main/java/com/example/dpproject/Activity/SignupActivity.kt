package com.example.dpproject.Activity

import android.content.Intent
import android.os.Bundle
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

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

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


 //   account creation with authentication
private fun createAccount(email: String, password: String) {
    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            auth.currentUser?.sendEmailVerification()
            Toast.makeText(this, "Please verify your Email", Toast.LENGTH_SHORT).show()
            saveUserData()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        else {
            Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
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
