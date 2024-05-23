package com.rios.spize.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.rios.spize.R
import com.rios.spize.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val chatRef: CollectionReference // Reference to the Firestore collection
) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMessage: TextView = itemView.findViewById(R.id.textViewMessage)
        val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.textViewMessage.text = message.message
        holder.textViewTimestamp.text = SimpleDateFormat("hh:mm a", Locale.getDefault())
            .format(Date(message.timestamp))
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    // Function to fetch messages from Firestore
    fun fetchMessages() {
        chatRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle error
                return@addSnapshotListener
            }

            messages.clear()
            snapshot?.forEach { document ->
                val message = document.toObject(ChatMessage::class.java)
                messages.add(message)
            }
            notifyDataSetChanged()
            recyclerView.scrollToPosition(messages.size - 1)
        }
    }

    fun addMessage(message: ChatMessage) {
        // Add message to Firestore
        chatRef.add(message)
            .addOnSuccessListener {
                // Message added successfully
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }
}
