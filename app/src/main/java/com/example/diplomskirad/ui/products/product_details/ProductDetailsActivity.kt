package com.example.diplomskirad.ui.products.product_details

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat
import androidx.room.Room
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.common.database.AppDatabase
import com.example.diplomskirad.common.preferences.LoginSharedPreferences
import com.example.diplomskirad.databinding.ActivityProductDetailsBinding
import com.example.diplomskirad.eventbus.UpdateCartEvent
import com.example.diplomskirad.listener.ICartLoadListener
import com.example.diplomskirad.model.Cart
import com.example.diplomskirad.model.Product
import com.example.diplomskirad.model.favorites.Favorites
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
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ProductDetailsActivity : AppCompatActivity(), ICartLoadListener {
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var database: DatabaseReference
    private lateinit var listener: ICartLoadListener

    private var selectedProductIdCart: String? = null
    private var selectedProduct: Product? = null
    private var productId: String? = null
    private var db: AppDatabase? = null
    private var sharedPreferences: LoginSharedPreferences? = null

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("product")
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "favorites").build()

        setContentView(R.layout.activity_product_details)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        productId = intent.getStringExtra("productId")
        database.addValueEventListener(postListener)
        sharedPreferences = LoginSharedPreferences(applicationContext)

        setListener()

        listener = this
    }

    private fun setListener() {
        binding.addToCart.setOnClickListener {
            addToCart()
        }

        binding.addToFavorites.setOnClickListener {
            addToFavorites()
        }

        binding.removeFromFavorites.setOnClickListener {
            removeFromFavorites()
        }

        binding.updateProduct.setOnClickListener {

        }
    }

    private fun addToFavorites() {
        executorService.execute {
            val favoriteDao = db?.favoritesDao()
            favoriteDao?.insertAll(Favorites(selectedProductIdCart))
            handler.post {
                database.child("${selectedProductIdCart}/addedToFavorites").setValue(true)
                Toast.makeText(applicationContext, "Added to favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeFromFavorites() {
        executorService.execute {
            val favoriteDao = db?.favoritesDao()
            selectedProductIdCart?.let { favoriteDao?.delete(it) }
            handler.post {
                database.child("${selectedProductIdCart}/addedToFavorites").setValue(false)
                Toast.makeText(applicationContext, "Removed from favorites", Toast.LENGTH_SHORT).show()
            }
        }
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
                        }?.addOnFailureListener { e ->
                            listener.onLoadCartError(e.message)
                        }
                } else {
                    val uuid = UUID.randomUUID().toString()

                    val cartModel = Cart(
                        uuid,
                        selectedProductIdCart,
                        selectedProduct?.productName,
                        selectedProduct?.image,
                        selectedProduct?.price,
                        1,
                        selectedProduct!!.price!!,
                        user?.email,
                        selectedProduct!!.categoryId
                    )

                    FirebaseDatabase.getInstance().getReference("cart").child(uuid).setValue(cartModel)
                        .addOnSuccessListener {
                            EventBus.getDefault().postSticky(UpdateCartEvent())
                            listener.onSuccessMessage("Product added to cart")
                        }.addOnFailureListener {
                            listener.onLoadCartError("Unable to add product")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("databaseError", error.message)
                listener.onLoadCartError(error.message)
            }
        })
    }

    private fun setUI(product: Product) {
        setVisibility(product)

        binding.productDescription.text = product.description
        binding.productName.text = product.productName
        binding.productPrice.text = StringBuilder("€").append(product.price.toString())
        binding.productCompany.text = product.companyId
        binding.productCategory.text = product.categoryId
        Picasso.get().load(product.image).placeholder(R.drawable.ic_no_image).into(binding.productImage)
    }

    private fun setVisibility(product: Product) {
        val user = Firebase.auth.currentUser

        if (product.addedToFavorites == true) {
            binding.addToFavorites.visibility = View.GONE
            binding.removeFromFavorites.visibility = View.VISIBLE
        } else {
            binding.addToFavorites.visibility = View.VISIBLE
            binding.removeFromFavorites.visibility = View.GONE
        }

        if (user != null) {
            binding.addToCart.visibility = View.VISIBLE
        } else {
            binding.addToCart.visibility = View.GONE
        }

        if (sharedPreferences?.getRole() == Constants().ADMIN_ROLE) {
            binding.updateProduct.visibility = View.VISIBLE
        } else {
            binding.updateProduct.visibility = View.GONE
        }
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