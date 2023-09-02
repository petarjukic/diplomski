package com.example.diplomskirad.ui.filtered_items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.databinding.FragmentFilteredItemsBinding
import com.example.diplomskirad.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FilteredItemsFragment : Fragment() {
    private val args: FilteredItemsFragmentArgs by navArgs()

    private var _binding: FragmentFilteredItemsBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference

    private var selectedCategory: String? = null
    private var selectedCompany: String? = null
    private var productList: MutableList<Product>? = null
    private var adapter: FilteredItemsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("product")
        _binding = FragmentFilteredItemsBinding.inflate(inflater, container, false)
        val args = this.arguments
        database.addValueEventListener(postListener)
        selectedCategory = args?.getString(Constants().FILTER_CATEGORY)
        selectedCompany = args?.getString(Constants().FILTER_COMPANY)
        productList = java.util.ArrayList()

        return binding.root
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val product = child.getValue(Product::class.java)

                if (product != null) {
                    if (selectedCategory != null) {
                        if (selectedCategory?.lowercase() == product.categoryId?.lowercase()) {
                            productList?.add(product)
                        }
                    } else {
                        if (selectedCompany?.lowercase() == product.companyId?.lowercase()) {
                            productList?.add(product)
                        }
                    }
                }
            }
            setAdapter()
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    private fun setAdapter() {
        adapter = productList?.let { FilteredItemsAdapter(it, this, null) }
        binding.rvProducts.layoutManager = GridLayoutManager(context, 2)
        binding.rvProducts.adapter = adapter
    }

    fun navigateToDetails(productId: String) {
        val bundle = bundleOf(Constants().SELECTED_PRODUCT_ID_TAG to productId)
        findNavController().navigate(R.id.productDetailsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        database.removeEventListener(postListener)
        _binding = null
    }

    companion object {
        val TAG = FilteredItemsFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): FilteredItemsFragment {
            val args = Bundle()
            val fragment = FilteredItemsFragment()
            fragment.arguments = args

            return fragment
        }
    }
}