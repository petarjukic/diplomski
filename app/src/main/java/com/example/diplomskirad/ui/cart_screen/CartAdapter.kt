package com.example.diplomskirad.ui.cart_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.model.Product
import com.squareup.picasso.Picasso

class CartAdapter(private val fragment: CartFragment, private val cartList: List<Product>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_product_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = cartList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        Picasso.get().load(cartList[position].image).placeholder(R.drawable.ic_no_image).into(holder.productImage)
        holder.productPrice.text = cartList[position].price.toString()
        holder.productTitle.text = cartList[position].productName
        holder.productItem.setOnClickListener {
            cartList[position].id?.let { it1 -> fragment.navigateToProductDetails(it1) }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView
        val productTitle: TextView
        val productPrice: TextView
        val productItem: ConstraintLayout

        init {
            productImage = view.findViewById(R.id.product_image)
            productTitle = view.findViewById(R.id.product_title)
            productPrice = view.findViewById(R.id.product_price)
            productItem = view.findViewById(R.id.product_item)
        }
    }
}