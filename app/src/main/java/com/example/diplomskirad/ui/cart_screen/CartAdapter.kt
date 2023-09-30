package com.example.diplomskirad.ui.cart_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.eventbus.UpdateCartEvent
import com.example.diplomskirad.model.Cart
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus

class CartAdapter(
    private val fragment: CartFragment,
    private val cartList: List<Cart>
) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_product_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = cartList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)

        Picasso.get().load(cartList[position].image).placeholder(R.drawable.ic_no_image).into(holder.productImage)
        holder.productPrice.text = StringBuilder("$").append(cartList[position].price.toString())
        holder.productTitle.text = cartList[position].name
        holder.quantity.text = cartList[position].quantity.toString()
        holder.productItem.setOnClickListener {
            cartList[position].id?.let { it1 -> fragment.navigateToProductDetails(it1) }
        }

        holder.btnAdd.setOnClickListener {
            addItemFromCart(holder, cartList[position])
        }

        holder.removeBtn.setOnClickListener {
            removeItemFromCart(holder, cartList[position], position)
        }
    }

    private fun addItemFromCart(holder: ViewHolder, cart: Cart) {
        cart.quantity += 1
        cart.totalPrice = cart.quantity * cart.price!!.toFloat()
        holder.quantity.text = cart.quantity.toString()

        updateFirebaseData(cart)
    }

    private fun removeItemFromCart(holder: ViewHolder, cart: Cart, position: Int) {
        if (cart.quantity > 1) {
            cart.quantity -= 1
            cart.totalPrice = cart.quantity * cart.price!!.toFloat()
            holder.quantity.text = cart.quantity.toString()
        } else {
            notifyItemRemoved(position)
        }

        updateFirebaseData(cart)
    }

    private fun updateFirebaseData(cart: Cart) {
        FirebaseDatabase.getInstance().getReference("cart").child(cart.id!!).setValue(cart)
            .addOnSuccessListener { EventBus.getDefault().postSticky(UpdateCartEvent()) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView
        val productTitle: TextView
        val productPrice: TextView
        val productItem: ConstraintLayout
        val quantity: TextView
        val btnAdd: ImageView
        val removeBtn: ImageView

        init {
            productImage = view.findViewById(R.id.product_image)
            productTitle = view.findViewById(R.id.product_title)
            productPrice = view.findViewById(R.id.product_price)
            productItem = view.findViewById(R.id.product_item)
            quantity = view.findViewById(R.id.number_of_items)
            btnAdd = view.findViewById(R.id.add_item)
            removeBtn = view.findViewById(R.id.remove_item)
        }
    }
}