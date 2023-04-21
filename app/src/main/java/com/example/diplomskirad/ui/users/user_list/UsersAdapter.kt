package com.example.diplomskirad.ui.users.user_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.model.User


class UsersAdapter(
    private val userList: List<User>?,
    private val fragment: UsersFragment
) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = userList!!.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.tvEmail.text = userList?.get(position)?.email
        holder.tvRole.text = userList?.get(position)?.role

        setListener(holder, position)
    }

    private fun setListener(holder: ViewHolder, position: Int) {
        holder.btnRemoveUser.setOnClickListener {
            fragment.removeUser(userList?.get(position)?.id)
        }

        holder.tvEmail.setOnClickListener {
            goToUserDetails(userList?.get(position))
        }
    }

    private fun goToUserDetails(user: User?) {
        val bundle = bundleOf(Constants().SELECTED_USER_ID_TAG to user?.id)
        fragment.navigateToDetails(bundle)
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