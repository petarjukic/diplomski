package com.example.diplomskirad.ui.products.product_details

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diplomskirad.R
import com.example.diplomskirad.databinding.ActivityProductDetailsBinding
import com.example.diplomskirad.eventbus.UpdateCartEvent
import com.example.diplomskirad.listener.ICartLoadListener
import com.example.diplomskirad.model.Cart
import com.example.diplomskirad.model.Product
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus


class ProductDetailsActivity : AppCompatActivity(), ICartLoadListener {
    private lateinit var binding: ActivityProductDetailsBinding

    private lateinit var database: DatabaseReference
    private var selectedProductIdCart: String? = null
    private var selectedProduct: Product? = null
    private lateinit var listener: ICartLoadListener
    private var productId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("product")

        setContentView(R.layout.activity_product_details)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        productId = intent.getStringExtra("productId")
        database.addValueEventListener(postListener)

        binding.addToCart.setOnClickListener {
            addToCart()
        }

        listener = this
    }

    private fun addToCart() {
        val user = Firebase.auth.currentUser
        val userCart = user?.uid?.let { FirebaseDatabase.getInstance().getReference("cart").child(it) }

        selectedProductIdCart?.let { userCart?.child(it) }?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val cartModel = snapshot.getValue(Cart::class.java)
                    val updatedData: MutableMap<String, Any> = HashMap()
                    cartModel!!.quantity = cartModel.quantity + 1
                    updatedData["quantity"] = cartModel.quantity
                    updatedData["price"] = cartModel.price!!.times(cartModel.quantity.toFloat())

                    selectedProduct!!.id?.let { userCart?.child(it) }?.updateChildren(updatedData)
                        ?.addOnSuccessListener {
                            EventBus.getDefault().postSticky(UpdateCartEvent())
                            listener.onSuccessMessage("Product added to cart")
                        }?.addOnFailureListener {
                            listener.onLoadCartError("Unable to add product")
                        }
                } else {
                    val cartModel = Cart()
                    cartModel.id = selectedProductIdCart
                    cartModel.name = selectedProduct?.productName
                    cartModel.image = selectedProduct?.image
                    cartModel.price = selectedProduct?.price
                    cartModel.quantity = 1
                    cartModel.totalPrice = cartModel.price!!

                    userCart?.child(selectedProductIdCart!!)?.setValue(cartModel)
                        ?.addOnSuccessListener {
                            EventBus.getDefault().postSticky(UpdateCartEvent())
                            listener.onSuccessMessage("Product added to cart")
                        }?.addOnFailureListener {
                            listener.onLoadCartError("Unable to add product")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onLoadCartError(error.message)
            }
        })
    }

    private fun setUI(product: Product) {
        binding.productDescription.text = product.description
        binding.productName.text = product.productName
        binding.productPrice.text = StringBuilder("€").append(product.price.toString())
        Picasso.get().load(product.image).placeholder(R.drawable.ic_no_image).into(binding.productImage)
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val product = child.getValue<Product>()
                if (product != null) {
                    if (product.id == productId) {
                        setUI(product = product)
                        break
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("databaseError", databaseError.message)
            throw databaseError.toException()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        database.removeEventListener(postListener)
    }

    override fun onLoadCartSuccess(cartList: List<Cart>) {
        var cartSum = 0
        for (cart in cartList) {
            cartSum += cart.quantity
        }
    }

    override fun onSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onLoadCartError(errorMessage: String?) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}