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
import com.example.diplomskirad.databinding.FragmentBestSellersBinding
import com.example.diplomskirad.model.Category
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BestSellerGenresFragment : Fragment() {
    private var _binding: FragmentBestSellersBinding? = null
    private val binding get() = _binding!!

    private var adapter: BestSellerGenreAdapter? = null
    private lateinit var database: DatabaseReference
    private var categoryList: MutableList<Category>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBestSellersBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference("category")
        categoryList = ArrayList()

        setAdapter()

        return binding.root
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {

            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("databaseError", databaseError.message)
        }
    }

    private fun setAdapter() {
        adapter = categoryList?.let { BestSellerGenreAdapter(it, this) }
        val llm = LinearLayoutManager(requireContext())
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rvBestsellers.layoutManager = llm
        binding.rvBestsellers.adapter = adapter
        database.addValueEventListener(postListener)
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
        val TAG = BestSellerGenresFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): BestSellerGenresFragment {
            val args = Bundle()
            val fragment = BestSellerGenresFragment()
            fragment.arguments = args

            return fragment
        }
    }
}