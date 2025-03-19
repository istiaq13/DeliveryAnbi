package com.example.dpproject.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import com.example.dpproject.R
import com.example.dpproject.domain.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class PlaceOrderActivity : AppCompatActivity() {
    private lateinit var foodNameEditText: EditText
    private lateinit var locationSpinner: Spinner
    private lateinit var quantityEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var additionalEditText: EditText
    private lateinit var paymentTextView: TextView
    private lateinit var placeOrderButton: Button
    private lateinit var cancelButton: Button
    private lateinit var voiceActivationButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var speechRecognizer: SpeechRecognizer

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        foodNameEditText = findViewById(R.id.foodNameText)
        locationSpinner = findViewById(R.id.locationSpinner)
        quantityEditText = findViewById(R.id.quantityEditText)
        priceEditText = findViewById(R.id.priceEditText)
        additionalEditText = findViewById(R.id.additionalEditText)
        paymentTextView = findViewById(R.id.paymentTextView)
        placeOrderButton = findViewById(R.id.placeOrderButton)
        cancelButton = findViewById(R.id.cancelButton)
        voiceActivationButton = findViewById(R.id.voiceActivationButton)

        val locations = arrayOf("Male Hall Of Residence", "Female Hall Of Residence")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locations)
        locationSpinner.adapter = adapter

        priceEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val price = s.toString().toDoubleOrNull() ?: 0.0
                val totalAmount = price + (price * 0.1)
                paymentTextView.text = "You Need To Pay ${totalAmount.toInt()} BDT For This Order"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cancelButton.setOnClickListener {
            finish()
        }

        placeOrderButton.setOnClickListener {
            val foodName = foodNameEditText.text.toString()
            val selectedLocation = locationSpinner.selectedItem.toString()
            val quantity = quantityEditText.text.toString().toIntOrNull() ?: 0
            val price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val additional = additionalEditText.text.toString()

            if (foodName.isEmpty() || selectedLocation == "Select Location" || quantity <= 0 || price <= 0) {
                Toast.makeText(this, "Please fill all required fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid ?: ""
            val order = Order(
                orderId = "",
                foodName = foodName,
                quantity = quantity,
                totalPrice = price,
                status = "Pending",
                time = "",
                userId = userId,
                location = selectedLocation
            )

            order.placeOrder()
            Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Check microphone permission
        checkMicrophonePermission()

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown error"
                }
                Toast.makeText(this@PlaceOrderActivity, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0].toLowerCase(Locale.ROOT)
                    processVoiceCommand(spokenText)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        voiceActivationButton.setOnClickListener {
            startSpeechRecognition()
        }

        // Auto-fill the form if a food name is passed via intent
        val foodName = intent.getStringExtra("foodName")
        foodName?.let {
            foodNameEditText.setText(it)
        }
    }

    private fun processVoiceCommand(spokenText: String) {
        when {
            spokenText.startsWith("item") -> {
                val itemName = spokenText.removePrefix("item").trim()
                foodNameEditText.setText(itemName)
            }
            spokenText.startsWith("quantity") -> {
                val quantity = spokenText.removePrefix("quantity").trim()
                quantityEditText.setText(quantity)
            }
            spokenText.startsWith("price") -> {
                val price = spokenText.removePrefix("price").trim()
                priceEditText.setText(price)
            }
            else -> {
                Toast.makeText(this, "Command not recognized", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your command (e.g., item Pizza, quantity 2, price 100)")
        speechRecognizer.startListening(intent)
    }

    private fun checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}