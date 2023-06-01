package com.example.diplomskirad.ui.filtered_items

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.model.Product
import com.squareup.picasso.Picasso

class FilteredItemsAdapter(private val productList: List<Product>, private val fragment: FilteredItemsFragment) :
    RecyclerView.Adapter<FilteredItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = productList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.productPrice.text = productList[position].price.toString()
        holder.productTitle.text = productList[position].productName
        Picasso.get().load(productList[position].image).into(holder.productImage)

        holder.product.setOnClickListener {
            Log.d("provjera", "AAAAAAA pozicija $position oiiii ${productList[position].id}")
            productList[position].id?.let { it1 -> fragment.navigateToDetails(it1) }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView
        val productTitle: TextView
        val productPrice: TextView
        val product: View

        init {
            productImage = view.findViewById(R.id.iv_product)
            productTitle = view.findViewById(R.id.tv_product_title)
            productPrice = view.findViewById(R.id.tv_product_price)
            product = view.findViewById(R.id.product)
        }
    }
}