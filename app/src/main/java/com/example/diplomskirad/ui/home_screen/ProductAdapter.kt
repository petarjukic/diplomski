package com.example.diplomskirad.ui.home_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.model.Product
import com.squareup.picasso.Picasso

class ProductAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = productList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.productPrice.text = productList[position].price.toString()
        holder.productTitle.text = productList[position].productName
        Picasso.get().load(productList[position].image).into(holder.productImage)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView
        val productTitle: TextView
        val productPrice: TextView

        init {
            productImage = view.findViewById(R.id.iv_product)
            productTitle = view.findViewById(R.id.tv_product_title)
            productPrice = view.findViewById(R.id.tv_product_price)
        }
    }
}