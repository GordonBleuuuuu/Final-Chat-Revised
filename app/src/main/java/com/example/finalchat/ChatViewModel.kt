package com.example.finalchat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val database = Firebase.database.reference
    val messages = mutableStateListOf<Message>()
    val messageInput = mutableStateOf("")

    init {
        checkUserAuthentication()
    }

    /**
     * Ensures the user is logged in before loading messages.
     * If the user is not logged in, they must sign in first.
     */
    private fun checkUserAuthentication() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Redirect user to login screen or sign in automatically (if needed)
            android.util.Log.e("Auth", "User not authenticated! Redirecting to login.")
        } else {
            loadMessages()
        }
    }

    /**
     * Loads messages from Firebase in real-time, ordered by timestamp.
     */
    private fun loadMessages() {
        database.child("messages").orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newMessages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                    messages.clear()
                    messages.addAll(newMessages)
                }

                override fun onCancelled(error: DatabaseError) {
                    android.util.Log.e("DatabaseError", "Failed to load messages: ${error.message}")
                }
            })
    }

    /**
     * Sends a message to Firebase.
     * Ensures the user is logged in before sending.
     */
    fun sendMessage() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            android.util.Log.e("ChatViewModel", "User not logged in! Cannot send message.")
            return
        }

        val text = messageInput.value.trim()
        if (text.isNotEmpty()) {
            val message = Message(
                senderId = currentUser.uid,
                message = text,
                timestamp = System.currentTimeMillis()
            )

            database.child("messages").push().setValue(message)
                .addOnSuccessListener {
                    messageInput.value = "" // Clear input after success
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("ChatViewModel", "Failed to send message", e)
                }
        }
    }
}
