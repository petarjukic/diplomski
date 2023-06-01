package com.example.diplomskirad.ui.products.product_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.databinding.FragmentProductDetailsBinding
import com.example.diplomskirad.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ProductDetailsFragment : Fragment() {
    val args: ProductDetailsFragmentArgs by navArgs()

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private var selectedProduct: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("product")
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        val args = this.arguments
        database.addValueEventListener(postListener)

        selectedProduct = args?.getString(Constants().SELECTED_PRODUCT_ID_TAG)
        binding.btnCheckout.setOnClickListener {
            // TODO implement add to cart
        }

        return binding.root
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val product = child.getValue(Product::class.java)

                if (product != null) {
                    if (selectedProduct == product.id) {
                        setUI(product)
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    private fun setUI(product: Product) {
        binding.productDescription.text = product.description
        binding.productName.text = product.productName
        binding.productPrice.text = product.price.toString()
        Picasso.get().load(product.image).into(binding.productImage)
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
}