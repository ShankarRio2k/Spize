package com.rios.spize.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.rios.spize.R
import com.rios.spize.adapter.ChatAdapter
import com.rios.spize.model.ChatMessage
import java.util.UUID

class DirectChatFragment : Fragment() {

    companion object {
        private const val ARG_USER_ID = "user_id"
        private const val ARG_USER_NAME = "user_name"

        fun newInstance(userId: String, userName: String?): DirectChatFragment {
            val fragment = DirectChatFragment()
            val args = Bundle().apply {
                putString(ARG_USER_ID, userId)
                putString(ARG_USER_NAME, userName)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var chatMessageAdapter: ChatAdapter
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_direct_chat, container, false)

        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbaar)
        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages)
        editTextMessage = view.findViewById(R.id.editTextMessage)
        buttonSend = view.findViewById(R.id.buttonSend)

        // Setup toolbar title
        val userName = arguments?.getString(ARG_USER_NAME)
        toolbar.title = "Chat with ${userName ?: "User"}"

// Define Firestore reference
        val db = Firebase.firestore
        val chatRef = db.collection("chats") // Replace "chats" with your Firestore collection name

// Setup RecyclerView and Adapter
        val chatMessageAdapter = ChatAdapter(requireContext(), recyclerViewMessages, chatRef)
        recyclerViewMessages.layoutManager = LinearLayoutManager(context)
        recyclerViewMessages.adapter = chatMessageAdapter

// Fetch messages from Firestore
        chatMessageAdapter.fetchMessages()


        // Handle send button click
        buttonSend.setOnClickListener {
            val messageText = editTextMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                editTextMessage.text.clear()
            }
        }

        return view
    }

    private fun sendMessage(messageText: String) {
        val userId = arguments?.getString(ARG_USER_ID) ?: return
        val userName = arguments?.getString(ARG_USER_NAME) ?: "Unknown"

        val chatMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = userId,
            senderName = userName,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )

        // Add message to the adapter and update RecyclerView
        chatMessageAdapter.addMessage(chatMessage)

        // Here you would also send the message to the server or handle other necessary logic
    }
}
