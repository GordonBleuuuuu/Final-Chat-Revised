// MainActivity.kt
package com.example.finalchat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")
        setContentView(R.layout.activity_user)
        Log.d("MainActivity", "content view set")

        auth = Firebase.auth
        database = Firebase.database.reference

        messageList = ArrayList()
        messageAdapter = MessageAdapter(messageList, auth.currentUser?.uid ?: "")

        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                Log.d("MainActivity", "authStateListener: user is null, redirecting to login")
                redirectToLogin()
            } else {
                Log.d("MainActivity", "authStateListener: user is signed in")
            }
        }

        auth.addAuthStateListener(authStateListener)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.d("MainActivity", "currentUser is null, redirecting to login")
            redirectToLogin()
            return
        } else {
            Log.d("MainActivity", "currentUser is not null: ${currentUser.uid}")
        }

        loadMessages()

        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun MessageAdapter(messageList: ArrayList<Message>, s: String): MessageAdapter { // Made some few args here to make the app functional

        return TODO("Provide the return value")
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(authStateListener)
        Log.d("MainActivity", "onDestroy called")
    }

    private fun loadMessages() {
        Log.d("MainActivity", "loadMessages started")
        database.child("messages").orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(Message::class.java)
                        message?.let { messageList.add(it) }
                    }
                    messageAdapter.notifyDataSetChanged() // Consider using more specific notify methods
                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                    Log.d("MainActivity", "loadMessages: data changed")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error loading messages", error.toException())
                    Toast.makeText(this@MainActivity, "Failed to load messages.", Toast.LENGTH_SHORT).show()
                    Log.d("MainActivity", "loadMessages: cancelled")
                }
            })
    }

    private fun sendMessage() {
        Log.d("MainActivity", "sendMessage started")
        val currentUser = auth.currentUser
        val messageText = messageEditText.text.toString().trim()

        if (currentUser == null) {
            Log.e("Firebase", "User not authenticated")
            Log.d("MainActivity", "sendMessage: currentUser is null")
            return
        }

        if (messageText.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity", "sendMessage: message text is empty")
            return
        }

        val message = Message(currentUser.uid, messageText, System.currentTimeMillis(), messageText)

        database.child("messages").push().setValue(message)
            .addOnSuccessListener {
                messageEditText.text.clear()
                chatRecyclerView.scrollToPosition(messageList.size - 1)
                Log.d("MainActivity", "sendMessage: message sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to send message", e)
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "sendMessage: failed to send message")
            }
    }

    private fun redirectToLogin() {
        Log.d("MainActivity", "redirectToLogin called")
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}