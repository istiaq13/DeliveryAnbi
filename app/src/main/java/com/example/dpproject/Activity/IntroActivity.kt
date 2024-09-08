package com.example.dpproject.Activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dpproject.R
import com.google.firebase.auth.FirebaseAuth

class IntroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        auth = FirebaseAuth.getInstance()

        // Handle edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signup: TextView = findViewById(R.id.signupBtn)
        val login: TextView = findViewById(R.id.loginBtn)

        // Check if the user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Check for internet connection before redirecting to DashboardActivity
            if (isInternetAvailable()) {
                // Hide the login and signup buttons if the user is logged in
                signup.visibility = TextView.GONE
                login.visibility = TextView.GONE

                // Redirect to DashboardActivity after 3 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish() // Close IntroActivity so the user cannot return to it
                }, 3000) // 3-second delay
            } else {
                // No internet connection, show toast message
                Toast.makeText(this, "No internet connection. Please connect to the internet.", Toast.LENGTH_LONG).show()
            }
        } else {
            // User is not logged in, show login and signup buttons
            // signup page opens through this click event
            signup.setOnClickListener {
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
            }

            // login page opens through this click event
            login.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // Helper function to check for internet connectivity
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
