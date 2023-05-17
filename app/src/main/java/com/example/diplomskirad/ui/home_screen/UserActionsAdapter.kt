package com.example.diplomskirad.ui.home_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R

class UserActionsAdapter(
    private val actionsList: List<String>,
    private val fragment: MainFragment
) :
    RecyclerView.Adapter<UserActionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_actions_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = actionsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.btnAction.text = actionsList[position]


        holder.btnAction.setOnClickListener {
            if (position == 0) {
                fragment.logoutUser()
            } else if (position == 1) {
                fragment.navigateToUserActions()
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnAction: Button

        init {
            btnAction = view.findViewById(R.id.btn_action)
        }
    }
}