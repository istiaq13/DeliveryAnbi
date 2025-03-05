package com.example.dpproject


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.example.dpproject.R

class UserProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var userName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var changeName: TextView
    private lateinit var changePassword: TextView
    private lateinit var makeChangesButton: Button
    private lateinit var profilePicture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Initialize Firebase
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance("https://dp-project-c2647-default-rtdb.firebaseio.com/")

        // Initialize views
        userName = findViewById(R.id.userName)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        changeName = findViewById(R.id.changeName)
        changePassword = findViewById(R.id.changePassword)
        makeChangesButton = findViewById(R.id.makeChangesButton)
        profilePicture = findViewById(R.id.profilePicture)

        // Load user data
        loadUserData()

        // Close button
        findViewById<TextView>(R.id.closeButton).setOnClickListener {
            finish()
        }

        // Change name
        changeName.setOnClickListener {
            userName.isEnabled = true
        }

        // Change password
        changePassword.setOnClickListener {
            password.isEnabled = true
        }

        // Make changes button
        makeChangesButton.setOnClickListener {
            updateUserData()
        }

        // Profile picture click
        profilePicture.setOnClickListener {
            openGallery()
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.reference.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            userName.setText(user.username ?: "N/A")
                            email.setText(user.email ?: "N/A")
                            password.setText(user.password ?: "N/A")
                        }
                    } else {
                        Toast.makeText(this@UserProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UserProfileActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val newName = userName.text.toString()
            val newPassword = password.text.toString()

            // Update name in Firebase Realtime Database
            database.reference.child("users").child(userId).child("username").setValue(newName)
                .addOnSuccessListener {
                    Toast.makeText(this, "Name updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update name", Toast.LENGTH_SHORT).show()
                }

            // Update password in Firebase Authentication
            if (newPassword.isNotEmpty()) {
                auth.currentUser?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            profilePicture.setImageURI(imageUri)
        }
    }
}

data class User(
    val username: String = "",
    val email: String = "",
    val password: String = ""
)