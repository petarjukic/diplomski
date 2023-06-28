package com.example.diplomskirad.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.model.Product
import com.squareup.picasso.Picasso

class SearchAdapter(private var productList: ArrayList<Product>) : RecyclerView.Adapter<SearchAdapter.ProductHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.search_item,
            parent, false
        )

        return ProductHolder(itemView)
    }

    fun filterList(filterList: ArrayList<Product>) {
        productList = filterList
        notifyDataSetChanged()
    }

    override fun getItemCount() = productList.size

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.productName.text = productList[position].productName
        Picasso.get().load(productList[position].image).placeholder(R.drawable.ic_no_image).into(holder.productImage)
    }

    class ProductHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
    }
}