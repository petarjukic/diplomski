package com.example.diplomskirad.ui.category.update_category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.databinding.FragmentUpdateCategoryBinding
import com.example.diplomskirad.model.Category
import com.google.firebase.database.*

class UpdateCategoryFragment : Fragment() {
    val args: UpdateCategoryFragmentArgs by navArgs()

    private var _binding: FragmentUpdateCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private var selectedCategory: Category? = null
    private var categoryId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("category")
        _binding = FragmentUpdateCategoryBinding.inflate(inflater, container, false)
        database.addValueEventListener(postListener)

        return binding.root
    }

    private fun setUI() {
        val args = this.arguments
        categoryId = args?.getString(Constants().SELECTED_CATEGORY_ID)
        // TODO implement update
//        binding.
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                selectedCategory = child.getValue(Category::class.java)
                if (categoryId == selectedCategory?.id) {
                    break
                }
            }

            setUI()
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    companion object {
        val TAG = UpdateCategoryFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): UpdateCategoryFragment {
            val args = Bundle()
            val fragment = UpdateCategoryFragment()
            fragment.arguments = args

            return fragment
        }
    }
}