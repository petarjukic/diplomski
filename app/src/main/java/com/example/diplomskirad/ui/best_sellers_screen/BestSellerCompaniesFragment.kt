package com.example.diplomskirad.ui.best_sellers_screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.databinding.FragmentBestSellerCompaniesBinding
import com.example.diplomskirad.model.Product
import com.example.diplomskirad.model.SoldItems
import com.example.diplomskirad.ui.best_sellers_screen.adapter.BestSellerCompanyAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class BestSellerCompaniesFragment : Fragment() {
    private var _binding: FragmentBestSellerCompaniesBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var companyDatabase: DatabaseReference

    private var soldItemsList: MutableList<SoldItems>? = null
    private var adapter: BestSellerCompanyAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("soldItems")
        companyDatabase = FirebaseDatabase.getInstance().getReference("product")

        _binding = FragmentBestSellerCompaniesBinding.inflate(inflater, container, false)

        database.addValueEventListener(postListener)
        soldItemsList = java.util.ArrayList()

        return binding.root
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val item = child.getValue<SoldItems>()
                if (item != null) {
                    soldItemsList?.add(item)
                }
            }
            getCompanies()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d("error", databaseError.message)
        }
    }

    private val companyPostListener = object : ValueEventListener {
        var products: MutableList<Product> = java.util.ArrayList()
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val product = child.getValue<Product>()
                if (product != null) {
                    products.add(product)
                }
            }
            setCompanies(products)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d("error", databaseError.message)
        }
    }

    private fun getCompanies() {
        companyDatabase.addValueEventListener(companyPostListener)
    }

    private fun setCompanies(products: List<Product>) {
        val foundedProducts = findProducts()
        val bestSellerCompanies1: MutableList<String> = mutableListOf()

        for (product in products) {
            if (foundedProducts.contains(product.productName) && product.companyId != null) {
                bestSellerCompanies1.add(product.companyId)
            }
        }

        setAdapter(bestSellerCompanies1.distinct())
    }

    private fun findProducts(): List<String> {
        val sortedList = hashMapOf<String, Int>()

        for (item in soldItemsList!!) {
            if (!sortedList.keys.contains(item.categoryId)) {
                sortedList[item.productName!!] = item.count!!
            } else {
                sortedList[item.productName!!] = sortedList.getValue(item.productName) + item.count!!
            }
        }

        val sortedMap = sortedList.toList().sortedBy { (_, v) -> v }.reversed().toMap()
        return sortedMap.keys.toList()
    }

    private fun setAdapter(companies: List<String>) {
        binding.loading.visibility = View.GONE

        adapter = BestSellerCompanyAdapter(companies, this)
        val llm = LinearLayoutManager(requireContext())
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rcBestSellerCompany.layoutManager = llm
        binding.rcBestSellerCompany.adapter = adapter
    }

    fun navigateToCompanyItems(companyId: String) {
        val bundle = bundleOf(Constants().FILTER_COMPANY to companyId)
        findNavController().navigate(R.id.filteredItemsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(postListener)
        companyDatabase.removeEventListener(companyPostListener)
        adapter = null
        _binding = null
    }

    companion object {
        val TAG = BestSellerCompaniesFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): BestSellerCompaniesFragment {
            val args = Bundle()
            val fragment = BestSellerCompaniesFragment()
            fragment.arguments = args

            return fragment
        }
    }
}