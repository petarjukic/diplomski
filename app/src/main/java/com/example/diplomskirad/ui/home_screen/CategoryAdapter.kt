package com.example.diplomskirad.ui.home_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R

class CategoryAdapter(private val categoryList: List<String>, private val fragment: MainFragment) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_actions_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = categoryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.btnAction.text = categoryList[position]
        holder.btnAction.setOnClickListener {
            fragment.navigateToFiltered(categoryList[position])
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnAction: Button

        init {
            btnAction = view.findViewById(R.id.btn_action)
        }
    }
}