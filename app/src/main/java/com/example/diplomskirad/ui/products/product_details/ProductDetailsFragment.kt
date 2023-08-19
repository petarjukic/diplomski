package com.example.diplomskirad.ui.products.product_details

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.HandlerCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.common.database.AppDatabase
import com.example.diplomskirad.common.preferences.LoginSharedPreferences
import com.example.diplomskirad.databinding.FragmentProductDetailsBinding
import com.example.diplomskirad.eventbus.UpdateCartEvent
import com.example.diplomskirad.listener.ICartLoadListener
import com.example.diplomskirad.model.Cart
import com.example.diplomskirad.model.Product
import com.example.diplomskirad.model.favorites.Favorites
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ProductDetailsFragment : Fragment(), ICartLoadListener {
    val args: ProductDetailsFragmentArgs by navArgs()

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var listener: ICartLoadListener

    private var selectedProductId: String? = null
    private var selectedProduct: Product? = null
    private var db: AppDatabase? = null
    private var sharedPreferences: LoginSharedPreferences? = null

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("product")
        auth = Firebase.auth
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "favorites").build()
        sharedPreferences = LoginSharedPreferences(requireContext())

        val args = this.arguments
        database.addValueEventListener(postListener)
        listener = this
        selectedProductId = args?.getString(Constants().SELECTED_PRODUCT_ID_TAG)

        setListener()

        return binding.root
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
            val bundle = bundleOf(Constants().SELECTED_PRODUCT_ID_TAG to selectedProductId)
            findNavController().navigate(R.id.updateProductFragment, bundle)
        }
    }

    private fun removeFromFavorites() {
        executorService.execute {
            val favoriteDao = db?.favoritesDao()
            selectedProductId?.let { favoriteDao?.delete(it) }
            handler.post {
                database.child("${selectedProductId}/addedToFavorites").setValue(false)
                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addToFavorites() {
        executorService.execute {
            val favoriteDao = db?.favoritesDao()
            favoriteDao?.insertAll(Favorites(selectedProductId))
            handler.post {
                database.child("${selectedProductId}/addedToFavorites").setValue(true)
                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addToCart() {
        val user = Firebase.auth.currentUser
        val userCart = user?.uid?.let { FirebaseDatabase.getInstance().getReference("cart").child(it) }

        selectedProductId?.let { userCart?.child(it) }?.addValueEventListener(object : ValueEventListener {
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
                            Log.d("databaseError", "${e.message}")
                            listener.onLoadCartError("Unable to add product")
                        }
                } else {
                    val uuid = UUID.randomUUID().toString()
                    val cartModel = Cart(
                        uuid,
                        selectedProductId,
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

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val product = child.getValue(Product::class.java)
                if (product != null) {
                    if (selectedProductId == product.id) {
                        selectedProduct = product
                        setUI(product)
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d("error", databaseError.message)
        }
    }

    private fun setUI(product: Product) {
        setVisibility(product)
        binding.productDescription.text = product.description
        binding.productName.text = product.productName
        binding.productPrice.text = StringBuilder("€").append(product.price.toString())
        binding.productCompany.text = product.companyId
        Picasso.get().load(product.image).placeholder(R.drawable.ic_no_image).into(binding.productImage)
    }

    private fun setVisibility(product: Product) {
        if (product.addedToFavorites == true) {
            binding.addToFavorites.visibility = View.GONE
            binding.removeFromFavorites.visibility = View.VISIBLE
        } else {
            binding.addToFavorites.visibility = View.VISIBLE
            binding.removeFromFavorites.visibility = View.GONE
        }

        if (sharedPreferences?.getRole() == Constants().ADMIN_ROLE) {
            binding.updateProduct.visibility = View.VISIBLE
        } else {
            binding.updateProduct.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(postListener)
        _binding = null
    }

    companion object {
        val TAG = ProductDetailsFragment::class.java.simpleName

        fun newInstance(): ProductDetailsFragment {
            val args = Bundle()
            val fragment = ProductDetailsFragment()
            fragment.arguments = args

            return fragment
        }
    }

    override fun onLoadCartSuccess(cartList: List<Cart>) {
        var cartSum = 0
        for (cart in cartList) {
            cartSum += cart.quantity
        }
    }

    override fun onSuccessMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onLoadCartError(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
}