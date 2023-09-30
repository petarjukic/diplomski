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
import com.example.diplomskirad.databinding.FragmentBestSellerCategoriesBinding
import com.example.diplomskirad.model.Product
import com.example.diplomskirad.model.SoldItems
import com.example.diplomskirad.ui.best_sellers_screen.adapter.BestSellerCategoryAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class BestSellerCategoriesFragment : Fragment() {
    private var _binding: FragmentBestSellerCategoriesBinding? = null
    private val binding get() = _binding!!

    private var adapter: BestSellerCategoryAdapter? = null
    private lateinit var database: DatabaseReference
    private var soldItemsList: MutableList<SoldItems>? = null
    private var sortedList: MutableList<Product>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBestSellerCategoriesBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference("soldItems")
        database.addValueEventListener(postListener)

        soldItemsList = java.util.ArrayList()
        sortedList = java.util.ArrayList()

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
            setAdapter()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("databaseError", databaseError.message)
        }
    }

    private fun setAdapter() {
        binding.loading.visibility = View.GONE

        val sortedCategories = findCategories()

        adapter = BestSellerCategoryAdapter(sortedCategories, this)
        val llm = LinearLayoutManager(requireContext())
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rcBestSellerGenre.layoutManager = llm
        binding.rcBestSellerGenre.adapter = adapter
    }

    private fun findCategories(): List<String> {
        val sortedList = hashMapOf<String, Int>()

        for (item in soldItemsList!!) {
            if (!sortedList.keys.contains(item.categoryId)) {
                sortedList[item.categoryId!!] = item.count!!
            } else {
                sortedList[item.categoryId!!] = sortedList.getValue(item.categoryId) + item.count!!
            }
        }

        val sortedMap = sortedList.toList().sortedBy { (_, v) -> v }.reversed().toMap()
        return sortedMap.keys.toList()
    }

    fun navigateToGenreItems(categoryId: String) {
        val bundle = bundleOf(Constants().FILTER_CATEGORY to categoryId)

        findNavController().navigate(R.id.filteredItemsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(postListener)
        adapter = null
        _binding = null
    }

    companion object {
        val TAG = BestSellerCategoriesFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): BestSellerCategoriesFragment {
            val args = Bundle()
            val fragment = BestSellerCategoriesFragment()
            fragment.arguments = args

            return fragment
        }
    }
}