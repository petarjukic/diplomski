package com.example.diplomskirad.ui.products.add_product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomskirad.R
import com.example.diplomskirad.databinding.FragmentAddProductBinding
import com.example.diplomskirad.model.Category
import com.example.diplomskirad.model.Product
import com.example.diplomskirad.ui.base_bottom_sheet.BottomSheetFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class AddProductFragment : Fragment() {
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryData: DatabaseReference
    private var categoryId: String? = null
    private var categoryList: MutableList<Category>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        categoryData = FirebaseDatabase.getInstance().getReference("category")
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        categoryList = ArrayList()
        categoryData.addValueEventListener(postListener)

        setListener()

        return binding.root
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val value: Category? = child.getValue(Category::class.java)
                if (value?.removed != null && !value.removed) {
                    categoryList?.add(value)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Toast.makeText(context, "Unable to load data.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setListener() {
        binding.btnAddProduct.setOnClickListener {
//            if (checkFields()) {
//                saveProduct()
//            }

            val lista: MutableList<String> = mutableListOf()
            if (categoryList != null) {
                for (category in categoryList!!) {
                    category.categoryName?.let { lista.add(it) }
                }
            }
            Log.d("provjera", "AAAa lisya ${categoryList?.size}")
            val bottomFragment = BottomSheetFragment.newInstance(lista)
            bottomFragment.show(childFragmentManager, BottomSheetFragment.TAG)
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.dropdown_roles,
            android.R.layout.simple_spinner_item,
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categoryDropdown.adapter = adapter
        }
    }

    private fun checkFields(): Boolean {
        if (binding.imageUrlEditText.text.toString() == "" ||
            binding.descriptionEditText.text.toString() == "" ||
            binding.priceEditText.text.toString() == "" ||
            binding.productNameEditText.text.toString() == ""
        ) {
            Toast.makeText(requireContext(), "Invalid input!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveProduct() {
        val uuid = UUID.randomUUID().toString()
        categoryId = binding.categoryDropdown.selectedItem.toString()
        val product = Product(
            uuid,
            binding.productNameEditText.text.toString(),
            binding.priceEditText.text.toString().toFloat(),
            categoryId,
            binding.imageUrlEditText.text.toString(),
            binding.descriptionEditText.text.toString()
        )

        categoryData.child("product").child(uuid).setValue(product)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoryList?.clear()
        categoryData.removeEventListener(postListener)
        _binding = null
    }

    companion object {
        val TAG = AddProductFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): AddProductFragment {
            val args = Bundle()
            val fragment = AddProductFragment()
            fragment.arguments = args

            return fragment
        }
    }
}