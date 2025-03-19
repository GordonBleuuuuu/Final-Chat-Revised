package com.example.finalchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private val userList: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emailTextView: TextView = itemView.findViewById(R.id.userEmail)
        val statusTextView: TextView = itemView.findViewById(R.id.userStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.emailTextView.text = user.email
        holder.statusTextView.text = if (user.isActive) "Active" else "Inactive"

        // Set status text color based on active/inactive state
        val statusColor = if (user.isActive) {
            ContextCompat.getColor(holder.itemView.context, android.R.color.holo_green_dark)
        } else {
            ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark)
        }
        holder.statusTextView.setTextColor(statusColor)

        // Handle user click event
        holder.itemView.setOnClickListener {
            onUserClick(user)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
