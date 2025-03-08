package com.example.dpproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dpproject.R
import com.example.dpproject.domain.ChatMessage
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private val messages = mutableListOf<ChatMessage>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun submitList(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderLayout: View = itemView.findViewById(R.id.senderLayout)
        private val senderMessage: TextView = itemView.findViewById(R.id.senderMessage)
        private val receiverLayout: View = itemView.findViewById(R.id.receiverLayout)
        private val receiverMessage: TextView = itemView.findViewById(R.id.receiverMessage)

        fun bind(message: ChatMessage) {
            if (message.senderId == currentUserId) {
                // Current user sent the message
                senderLayout.visibility = View.VISIBLE
                receiverLayout.visibility = View.GONE
                senderMessage.text = message.message
            } else {
                // Other user sent the message
                senderLayout.visibility = View.GONE
                receiverLayout.visibility = View.VISIBLE
                receiverMessage.text = message.message
            }
        }
    }
}