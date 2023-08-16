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
import com.example.diplomskirad.ui.best_sellers_screen.adapter.BestSellerAdapter
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
    private var soldItemsList: MutableList<SoldItems>? = null
    private var sortedList: MutableList<Product>? = null
    private var adapter: BestSellerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("soldItems")
        _binding = FragmentBestSellersBinding.inflate(inflater, container, false)
        database.addValueEventListener(postListener)

        soldItemsList = java.util.ArrayList()
        sortedList = java.util.ArrayList()

        return binding.root
    }

    fun navigateToDetails(productId: String) {
        val bundle = bundleOf(Constants().SELECTED_PRODUCT_ID_TAG to productId)
        findNavController().navigate(R.id.productDetailsFragment, bundle)
    }

    private fun setAdapter() {
        soldItemsList?.sortByDescending { it.count }
        if (soldItemsList != null && soldItemsList!!.size > 0) {
            if (soldItemsList?.size!! >= 5) {
                for (i in 0..5) {
                    val product = Product(
                        id = soldItemsList!![i].id,
                        image = soldItemsList!![i].image,
                        productName = soldItemsList!![i].productName,
                        price = soldItemsList!![i].price,
                        categoryId = soldItemsList!![i].categoryId
                    )
                    sortedList?.add(product)
                }
            } else {
                for (i in 0 until soldItemsList!!.size) {
                    val product = Product(
                        id = soldItemsList!![i].id,
                        image = soldItemsList!![i].image,
                        productName = soldItemsList!![i].productName,
                        price = soldItemsList!![i].price,
                        categoryId = soldItemsList!![i].categoryId
                    )
                    sortedList?.add(product)
                }
            }
        }

        adapter = BestSellerAdapter(soldItemsList!!.toList(), this)
        binding.rvBestsellers.layoutManager = GridLayoutManager(context, 2)
        binding.rvBestsellers.adapter = adapter
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val item = child.getValue<SoldItems>()

                if (item != null) {
                    soldItemsList?.add(item)
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