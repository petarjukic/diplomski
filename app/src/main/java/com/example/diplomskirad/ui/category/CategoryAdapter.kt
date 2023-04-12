package com.example.diplomskirad.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.model.Category

class CategoryAdapter(
    private val categoryList: List<Category>?,
    private val fragment: CategoryListFragment
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = categoryList!!.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCategoryName.text = categoryList?.get(position)?.categoryName
        holder.btnRemoveItem.setOnClickListener {
            categoryList?.get(position)?.id?.let { it1 -> fragment.removeCategoryItem(it1) }
        }
        holder.tvCategoryName.setOnClickListener {
            val bundle = bundleOf(Constants().SELECTED_CATEGORY_ID to categoryList?.get(position)?.id)
            fragment.navigateToDetails(bundle)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoryName: TextView
        val btnRemoveItem: Button

        init {
            tvCategoryName = view.findViewById(R.id.category)
            btnRemoveItem = view.findViewById(R.id.remove_category)
        }
    }
}