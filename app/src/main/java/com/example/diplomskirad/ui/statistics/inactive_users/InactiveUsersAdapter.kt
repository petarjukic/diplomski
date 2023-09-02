package com.example.diplomskirad.ui.statistics.inactive_users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.model.User

class InactiveUsersAdapter(private val inactiveUsers: List<User>, private val fragment: InactiveUsersFragment) :
    RecyclerView.Adapter<InactiveUsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inactive_users_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = inactiveUsers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.email.text = inactiveUsers[position].email
        holder.firstName.text = inactiveUsers[position].firstName
        holder.lastName.text = inactiveUsers[position].lastName
        holder.role.text = inactiveUsers[position].role

        holder.deleteUserBtn.setOnClickListener {
            fragment.deleteUser(inactiveUsers[position])
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val email: TextView
        val firstName: TextView
        val lastName: TextView
        val role: TextView
        val deleteUserBtn: Button

        init {
            email = view.findViewById(R.id.inactive_email)
            firstName = view.findViewById(R.id.inactive_first_name)
            lastName = view.findViewById(R.id.inactive_last_name)
            role = view.findViewById(R.id.inactive_role)
            deleteUserBtn = view.findViewById(R.id.inactive_delete_user)
        }
    }
}