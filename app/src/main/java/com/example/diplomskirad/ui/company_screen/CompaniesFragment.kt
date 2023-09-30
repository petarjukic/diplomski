package com.example.diplomskirad.ui.company_screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.common.preferences.LoginSharedPreferences
import com.example.diplomskirad.databinding.FragmentCompaniesBinding
import com.example.diplomskirad.model.Company
import com.example.diplomskirad.ui.company_screen.adapter.CompanyAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CompaniesFragment : Fragment() {
    private var _binding: FragmentCompaniesBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var companies: ArrayList<Company>

    private var sharedPreferences: LoginSharedPreferences? = null
    private var adapter: CompanyAdapter? = null
    private var isAdmin: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("company")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompaniesBinding.inflate(inflater, container, false)

        companies = ArrayList()
        sharedPreferences = LoginSharedPreferences(requireContext())

        if (sharedPreferences?.getRole() == Constants().ADMIN_ROLE) {
            isAdmin = true
            binding.btnAddCompany.visibility = View.VISIBLE
        }

        binding.btnAddCompany.setOnClickListener {
            findNavController().navigate(R.id.addCompanyFragment)
        }

        setAdapter()
        return binding.root
    }

    private fun setAdapter() {
        val llm = LinearLayoutManager(requireContext())
        adapter = CompanyAdapter(companies, this, isAdmin)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rcCompanies.layoutManager = llm
        binding.rcCompanies.adapter = adapter
        database.addValueEventListener(postListener)
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val value: Company? = child.getValue(Company::class.java)
                if (value != null && !value.removed!!) {
                    companies.add(value)
                }
            }

            adapter?.notifyDataSetChanged()
            binding.loading.visibility = View.GONE
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d("databaseError", databaseError.message)
            Toast.makeText(context, "Unable to load data.", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeCompany(companyId: String) {
        database.child("${companyId}/removed").setValue(true)

        findNavController().navigate(R.id.companiesFragment)
    }

    fun navigateToDetails(bundle: Bundle) {
        findNavController().navigate(R.id.updateCompanyFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        companies.clear()
        database.removeEventListener(postListener)
        adapter = null
        _binding = null
    }

    companion object {
        val TAG = CompaniesFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): CompaniesFragment {
            val arg = Bundle()
            val fragment = CompaniesFragment()
            fragment.arguments = arg

            return fragment
        }
    }
}