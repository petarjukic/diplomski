package com.example.diplomskirad.ui.best_sellers_screen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.model.Category
import com.example.diplomskirad.ui.best_sellers_screen.BestSellerGenresFragment

class BestSellerGenreAdapter(private val genreList: List<Category>, private val fragment: BestSellerGenresFragment) :
    RecyclerView.Adapter<BestSellerGenreAdapter.BestSellerGenreHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestSellerGenreHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.best_seller_genre_item, parent, false)

        return BestSellerGenreHolder(view)
    }

    override fun getItemCount() = genreList.size

    override fun onBindViewHolder(holder: BestSellerGenreHolder, position: Int) {
        holder.categoryName.text = genreList[position].categoryName

        holder.genreItem.setOnClickListener {
            genreList[position].id?.let { it1 -> fragment.navigateToGenreItems(it1) }
        }
    }

    class BestSellerGenreHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView
        val genreItem: View

        init {
            categoryName = view.findViewById(R.id.category_name)
            genreItem = view.findViewById(R.id.category_item)
        }
    }
}