package com.example.diplomskirad.ui.best_sellers_screen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.model.SoldItems
import com.example.diplomskirad.ui.best_sellers_screen.BestSellersFragment
import com.squareup.picasso.Picasso

class BestSellerAdapter(
    private val filteredItems: List<SoldItems>,
    private val bestSellersFragment: BestSellersFragment?
) : RecyclerView.Adapter<BestSellerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = filteredItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.productPrice.text = StringBuilder("€").append(filteredItems[position].price.toString())
        holder.productTitle.text = filteredItems[position].productName
        Picasso.get().load(filteredItems[position].image).placeholder(R.drawable.ic_no_image).into(holder.productImage)

        holder.product.setOnClickListener {
            filteredItems[position].id?.let { it1 -> bestSellersFragment?.navigateToDetails(it1) }
            filteredItems[position].id?.let { it1 -> bestSellersFragment?.navigateToDetails(it1) }
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