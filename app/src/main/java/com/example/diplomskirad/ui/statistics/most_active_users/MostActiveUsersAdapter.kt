package com.example.diplomskirad.ui.statistics.most_active_users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R

class MostActiveUsersAdapter(private val soldItems: List<String>, private val fragment: MostActiveUsersFragment) :
    RecyclerView.Adapter<MostActiveUsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.best_seller_genre_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = soldItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userEmail.text = soldItems[position]

        holder.userItem.setOnClickListener {
            soldItems[position].let { it1 -> fragment.navigateUserDetails(it1) }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userEmail: TextView
        val userItem: View

        init {
            userEmail = view.findViewById(R.id.category_name)
            userItem = view.findViewById(R.id.category_item)
        }
    }
}