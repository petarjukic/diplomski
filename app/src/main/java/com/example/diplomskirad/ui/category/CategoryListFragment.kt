package com.example.diplomskirad.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.R
import com.example.diplomskirad.databinding.FragmentCategoryListBinding
import com.example.diplomskirad.model.Category
import com.google.firebase.database.*

class CategoryListFragment : Fragment() {
    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private var categoryList: MutableList<Category>? = null
    private var adapter: CategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("category")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)

        categoryList = ArrayList()

        val llm = LinearLayoutManager(requireContext())
        adapter = CategoryAdapter(categoryList, this)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rcCategoryList.layoutManager = llm
        binding.rcCategoryList.adapter = adapter
        database.addValueEventListener(postListener)

        binding.btnAddCategory.setOnClickListener {
            findNavController().navigate(R.id.add_category_fragment)
        }

        return binding.root
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val value: Category? = child.getValue(Category::class.java)

                if (value != null && !value.isRemoved) {
                    categoryList?.add(value)
                }
            }

            adapter?.notifyDataSetChanged()
//            binding.loading.visibility = View.GONE
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Toast.makeText(context, "Unable to load data.", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeCategoryItem(categoryId: String) {
        database.child("${categoryId}/isRemoved").setValue(true)
    }

    fun navigateToDetails(bundle: Bundle) {
        findNavController().navigate(R.id.updateCategoryFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }

    companion object {
        val TAG = CategoryListFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): CategoryListFragment {
            val args = Bundle()
            val fragment = CategoryListFragment()
            fragment.arguments = args

            return fragment
        }
    }
}