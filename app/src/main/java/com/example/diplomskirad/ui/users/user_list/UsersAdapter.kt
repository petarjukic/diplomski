package com.example.diplomskirad.ui.users.user_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.model.User

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
    private val userList: MutableList<User>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvEmail.text = userList[position].email
        holder.tvRole.text = userList[position].role
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmail: TextView
        val tvRole: TextView

        init {
            // Define click listener for the ViewHolder's View
            tvEmail = view.findViewById(R.id.user_email)
            tvRole = view.findViewById(R.id.user_role)
        }
    }

    init {
        userList = ArrayList()
    }
}