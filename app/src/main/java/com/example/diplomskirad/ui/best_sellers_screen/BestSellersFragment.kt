package com.example.diplomskirad.ui.best_sellers_screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.databinding.FragmentBestSellersBinding
import com.example.diplomskirad.model.Product
import com.example.diplomskirad.model.SoldItems
import com.example.diplomskirad.ui.filtered_items.FilteredItemsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class BestSellersFragment : Fragment() {
    private var _binding: FragmentBestSellersBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private var productList: MutableList<Product>? = null
    private var soldItemsList: MutableList<SoldItems>? = null
    private var sortedList: MutableList<Product>? = null
    private var adapter: FilteredItemsAdapter? = null
    private var bestSellersMap = hashMapOf<Int, Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // TODO check the name of table
        database = FirebaseDatabase.getInstance().getReference("sold_items")
        _binding = FragmentBestSellersBinding.inflate(inflater, container, false)
        database.addValueEventListener(postListener)

        productList = java.util.ArrayList()
        soldItemsList = java.util.ArrayList()
        sortedList = java.util.ArrayList()

        return binding.root
    }

    fun navigateToDetails(productId: String) {
        val bundle = bundleOf(Constants().SELECTED_PRODUCT_ID_TAG to productId)
        findNavController().navigate(R.id.productDetailsFragment, bundle)
    }

    private fun findBestSellers(): List<Product> {
        var finalList: List<Product> = listOf()

        if (productList != null) {
//            for (product in productList!!) {
//
//            }
        }

        return finalList
    }

    private fun setAdapter() {
        //TODO check result of bestsellers
        val filteredList = findBestSellers()
        soldItemsList?.sortByDescending { it.count }
        if (soldItemsList != null && soldItemsList!!.size > 0) {
            if (soldItemsList?.size!! >= 5) {
                for (i in 0..5) {
                    val product = Product(
                        id = soldItemsList!![i].id,
                        image = soldItemsList!![i].image,
                        productName = soldItemsList!![i].productName,
                        price = soldItemsList!![i].price,
                        description = soldItemsList!![i].description,
                        categoryId = soldItemsList!![i].categoryId
                    )
                    sortedList?.add(product)
                }
            } else {
                for (i in 0..soldItemsList!!.size) {
                    val product = Product(
                        id = soldItemsList!![i].id,
                        image = soldItemsList!![i].image,
                        productName = soldItemsList!![i].productName,
                        price = soldItemsList!![i].price,
                        description = soldItemsList!![i].description,
                        categoryId = soldItemsList!![i].categoryId
                    )
                    sortedList?.add(product)
                }
            }
        }

        adapter = sortedList?.let { FilteredItemsAdapter(it, null, this) }
        binding.rvBestsellers.layoutManager = GridLayoutManager(context, 2)
        binding.rvBestsellers.adapter = adapter
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val item = child.getValue<SoldItems>()

                if (item != null) {
                    soldItemsList?.add(item)
                    val product = Product(
                        price = item.price,
                        productName = item.productName,
                        description = item.description,
                        image = item.image,
                        categoryId = item.categoryId
                    )
                    productList?.add(product)
                }
            }
            setAdapter()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("databaseError", databaseError.message)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(postListener)
        adapter = null
        _binding = null
    }

    companion object {
        val TAG = BestSellersFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): BestSellersFragment {
            val args = Bundle()
            val fragment = BestSellersFragment()
            fragment.arguments = args

            return fragment
        }
    }
}