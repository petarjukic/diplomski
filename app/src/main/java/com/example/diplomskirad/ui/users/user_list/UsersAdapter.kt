package com.example.diplomskirad.ui.users.user_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.model.User

class UsersAdapter(private val userList: List<User>?, private val context: Context?) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = userList!!.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvEmail.text = userList?.get(position)?.email
        holder.tvRole.text = userList?.get(position)?.role

        setListener(holder, position)
    }

    private fun setListener(holder: ViewHolder, position: Int) {
        holder.btnRemoveUser.setOnClickListener {
            removeUser(userList?.get(position))
        }

        holder.tvEmail.setOnClickListener {
            goToUserDetails()
        }
    }

    private fun goToUserDetails() {

    }

    private fun removeUser(user: User?) {
        //TODO implement hiding user
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmail: TextView
        val tvRole: TextView
        val btnRemoveUser: Button

        init {
            tvEmail = view.findViewById(R.id.user_email)
            tvRole = view.findViewById(R.id.user_role)
            btnRemoveUser = view.findViewById(R.id.remove_user)
        }
    }
}