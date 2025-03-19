package com.example.finalchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ActivityUser : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var usersRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("users")

        usersRecyclerView = findViewById(R.id.usersRecyclerView)
        usersRecyclerView.layoutManager = LinearLayoutManager(this)
        userList = ArrayList()

        userAdapter = UserAdapter(userList) { user ->
            openChat(user)
        }

        usersRecyclerView.adapter = userAdapter

        loadUsers()
    }

    private fun loadUsers() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to load users", error.toException())
            }
        })
    }

    private fun openChat(user: User) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USER_ID", user.uid)
        intent.putExtra("USER_EMAIL", user.email)
        startActivity(intent)
    }
}
