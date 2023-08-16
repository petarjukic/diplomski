package com.example.diplomskirad.ui.company_screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.databinding.FragmentUpdateCompanyBinding
import com.example.diplomskirad.model.Company
import com.example.diplomskirad.ui.category.update_category.UpdateCategoryFragmentArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UpdateCompanyFragment : Fragment() {
    val args: UpdateCategoryFragmentArgs by navArgs()

    private var _binding: FragmentUpdateCompanyBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private var selectedCompany: Company? = null
    private var companyId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("company")
        _binding = FragmentUpdateCompanyBinding.inflate(inflater, container, false)

        val args = this.arguments
        companyId = args?.getString(Constants().SELECTED_CATEGORY_ID)
        database.addValueEventListener(postListener)

        binding.btnUpdateCompany.setOnClickListener {
            if (binding.companyNameEditText.text.toString() != "") {
                updateCompany()
            }
        }

        return binding.root
    }

    private fun updateCompany() {
        database.child("${companyId}/companyName").setValue(binding.companyNameEditText.text.toString())

        findNavController().popBackStack()
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                selectedCompany = child.getValue(Company::class.java)
                if (companyId == selectedCompany?.id) {
                    break
                }
            }

            setUI()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d("databaseError", databaseError.message)
        }
    }

    private fun setUI() {
        binding.companyNameEditText.setText(selectedCompany?.companyName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(postListener)
        _binding = null
    }

    companion object {
        val TAG = UpdateCompanyFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): UpdateCompanyFragment {
            val arg = Bundle()
            val fragment = UpdateCompanyFragment()
            fragment.arguments = arg

            return fragment
        }
    }
}