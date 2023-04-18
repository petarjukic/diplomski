package com.example.diplomskirad.ui.category.add_category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomskirad.databinding.FragmentAddCategoryBinding
import com.example.diplomskirad.model.Category
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AddCategoryFragment : Fragment() {
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)

        binding.btnAddCategory.setOnClickListener {
            saveCategory()
        }

        return binding.root
    }

    private fun saveCategory() {
        val uuid = UUID.randomUUID().toString()
        val category = Category(uuid, binding.tvCategoryName.text.toString(), false)
        database.child("category").child(uuid).setValue(category)

        findNavController().popBackStack()
    }

    companion object {
        val TAG = AddCategoryFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): AddCategoryFragment {
            val args = Bundle()
            val fragment = AddCategoryFragment()
            fragment.arguments = args

            return fragment
        }
    }
}