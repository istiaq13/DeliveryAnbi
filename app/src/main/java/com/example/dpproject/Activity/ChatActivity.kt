package com.example.dpproject.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dpproject.R
import com.example.dpproject.adapter.ChatAdapter
import com.example.dpproject.domain.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var chatRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var chatId: String = ""
    private var receiverId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Get chatId and receiverId from intent
        chatId = intent.getStringExtra("chatId") ?: ""
        receiverId = intent.getStringExtra("receiverId") ?: ""

        // Initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        // Setup RecyclerView
        chatAdapter = ChatAdapter()
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        // Load chat messages
        loadChatMessages()

        // Handle send button click
        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun loadChatMessages() {
        chatRef = database.getReference("chats").child(chatId).child("messages")
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<ChatMessage>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(ChatMessage::class.java)
                    message?.let { messages.add(it) }
                }
                chatAdapter.submitList(messages)
                chatRecyclerView.scrollToPosition(messages.size - 1) // Scroll to the latest message
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun sendMessage() {
        val messageText = messageEditText.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val senderId = auth.currentUser?.uid ?: ""
            val timestamp = System.currentTimeMillis().toString()

            val message = ChatMessage(
                messageId = chatRef.push().key ?: "",
                senderId = senderId,
                receiverId = receiverId,
                message = messageText,
                timestamp = timestamp
            )

            // Save message to Firebase
            chatRef.child(message.messageId).setValue(message).addOnCompleteListener {
                if (it.isSuccessful) {
                    messageEditText.text.clear()
                }
            }
        }
    }
}