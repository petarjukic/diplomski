package com.example.diplomskirad.ui.company_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomskirad.databinding.FragmentAddCompanyBinding
import com.example.diplomskirad.model.Company
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AddCompanyFragment : Fragment() {
    private var _binding: FragmentAddCompanyBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCompanyBinding.inflate(inflater, container, false)

        binding.btnAddCompany.setOnClickListener {
            saveCompany()
        }

        return binding.root
    }

    private fun saveCompany() {
        val uuid = UUID.randomUUID().toString()
        val category = Company(uuid, binding.tvCompanyName.text.toString(), false)
        database.child("company").child(uuid).setValue(category)

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = AddCompanyFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): AddCompanyFragment {
            val arg = Bundle()
            val fragment = AddCompanyFragment()
            fragment.arguments = arg

            return fragment
        }
    }
}