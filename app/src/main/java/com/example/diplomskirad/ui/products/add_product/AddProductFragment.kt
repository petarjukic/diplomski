package com.example.diplomskirad.ui.products.add_product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomskirad.databinding.FragmentAddProductBinding
import com.example.diplomskirad.model.Category
import com.example.diplomskirad.model.Company
import com.example.diplomskirad.model.Product
import com.example.diplomskirad.ui.base_bottom_sheet.BottomSheetFragment
import com.example.diplomskirad.ui.base_bottom_sheet.company.CompanyBottomSheetFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class AddProductFragment : Fragment(), BottomSheetFragment.BottomSheetListener,
    CompanyBottomSheetFragment.BottomSheetListener {
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryData: DatabaseReference
    private lateinit var companyData: DatabaseReference
    private var categoryId: String? = null
    private var categoryList: MutableList<Category>? = null
    private var companies: MutableList<Company>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        categoryData = FirebaseDatabase.getInstance().getReference("category")
        companyData = FirebaseDatabase.getInstance().getReference("company")
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        categoryList = ArrayList()
        companies = ArrayList()
        categoryData.addValueEventListener(postListener)
        companyData.addValueEventListener(companiesPostListener)

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

    private val companiesPostListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val value: Company? = child.getValue(Company::class.java)
                if (value?.removed != null && !value.removed) {
                    companies?.add(value)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d("databaseError", databaseError.message)
        }
    }

    private fun setListener() {
        binding.btnAddProduct.setOnClickListener {
            if (checkFields()) {
                saveProduct()
            }
        }

        binding.categoryDropdown.setOnClickListener {
            val data: MutableList<String> = mutableListOf()
            if (categoryList != null) {
                for (category in categoryList!!) {
                    category.categoryName?.let { data.add(it) }
                }
            }

            val bottomFragment: BottomSheetFragment = if (binding.categoryDropdown.text.toString() != "") {
                BottomSheetFragment.newInstance(data, binding.categoryDropdown.text.toString())

            } else {
                BottomSheetFragment.newInstance(data, null)
            }

            bottomFragment.show(childFragmentManager, BottomSheetFragment.TAG)
            bottomFragment.setListener(this)
        }

        binding.companyDropdown.setOnClickListener {
            val data: MutableList<String> = mutableListOf()
            if (companies != null) {
                for (company in companies!!) {
                    company.companyName?.let { data.add(it) }
                }
            }

            val bottomCompanyFragment: CompanyBottomSheetFragment = if (binding.companyDropdown.text.toString() != "") {
                CompanyBottomSheetFragment.newInstance(data, binding.companyDropdown.text.toString())

            } else {
                CompanyBottomSheetFragment.newInstance(data, null)
            }

            bottomCompanyFragment.show(childFragmentManager, CompanyBottomSheetFragment.TAG)
            bottomCompanyFragment.setListener(this)
        }
    }

    private fun checkFields(): Boolean {
        if (binding.imageUrlEditText.text.toString() == "" ||
            binding.descriptionEditText.text.toString() == "" ||
            binding.priceEditText.text.toString() == "" ||
            binding.productNameEditText.text.toString() == "" ||
            binding.categoryDropdown.text.toString() == "" ||
            binding.companyDropdown.text.toString() == ""
        ) {
            Toast.makeText(requireContext(), "Invalid input!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveProduct() {
        val uuid = UUID.randomUUID().toString()
        categoryId = binding.categoryDropdown.text.toString()

        val product = Product(
            uuid,
            binding.productNameEditText.text.toString(),
            binding.priceEditText.text.toString().toFloat(),
            categoryId,
            binding.imageUrlEditText.text.toString(),
            binding.descriptionEditText.text.toString(),
            binding.companyDropdown.text.toString(),
            false
        )

        categoryData.child("product").child(uuid).setValue(product)
        findNavController().popBackStack()
    }

    override fun onCategoryItemClicked(title: String) {
        binding.categoryDropdown.text = title
    }

    override fun onCompanyItemClicked(title: String) {
        binding.companyDropdown.text = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoryList?.clear()
        categoryData.removeEventListener(postListener)
        companyData.removeEventListener(companiesPostListener)
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