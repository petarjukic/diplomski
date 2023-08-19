package com.example.diplomskirad.ui.products.update_product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.databinding.FragmentUpdateProductBinding
import com.example.diplomskirad.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UpdateProductFragment : Fragment() {
    val args: UpdateProductFragmentArgs by navArgs()

    private var _binding: FragmentUpdateProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference

    private var productId: String? = null
    private var selectedProduct: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateProductBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference("product")

        val args = this.arguments
        productId = args?.getString(Constants().SELECTED_PRODUCT_ID_TAG)
        database.addValueEventListener(postListener)

        binding.btnUpdateProduct.setOnClickListener {
            updateProduct()
        }

        return binding.root
    }

    private fun setUI() {
        binding.productNameEditText.setText(selectedProduct?.productName)
        binding.priceEditText.setText(selectedProduct?.price.toString())
        binding.descriptionEditText.setText(selectedProduct?.description)
        binding.imageEditText.setText(selectedProduct?.image)
        binding.categoryDropdown.text = selectedProduct?.categoryId
        binding.companyDropdown.text = selectedProduct?.companyId
    }

    private fun updateProduct() {
        database.child("${productId}/productName").setValue(binding.productNameEditText.text.toString())
        database.child("${productId}/price").setValue(binding.priceEditText.text.toString())
        database.child("${productId}/description").setValue(binding.descriptionEditText.text.toString())
        database.child("${productId}/image").setValue(binding.imageEditText.text.toString())
        database.child("${productId}/categoryId").setValue(binding.categoryDropdown.text.toString())
        database.child("${productId}/companyId").setValue(binding.companyDropdown.text.toString())

        findNavController().popBackStack()
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                selectedProduct = child.getValue(Product::class.java)
                if (productId == selectedProduct?.id) {
                    break
                }
            }

            setUI()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d("databaseError", databaseError.message)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = UpdateProductFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): UpdateProductFragment {
            val args = Bundle()
            val fragment = UpdateProductFragment()
            fragment.arguments = args

            return fragment
        }
    }
}
