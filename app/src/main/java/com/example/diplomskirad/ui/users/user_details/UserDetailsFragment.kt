package com.example.diplomskirad.ui.users.user_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.databinding.FragmentUserDetailsBinding
import com.example.diplomskirad.model.User
import com.google.firebase.database.*
import java.util.*


class UserDetailsFragment : Fragment() {
    val args: UserDetailsFragmentArgs by navArgs()

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private var selectedUser: User? = null
    private var userId: String? = null
    private var roleToUpdate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("users")
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        database.addValueEventListener(postListener)

        setupListener()

        return binding.root
    }

    private fun setUI() {
        val args = this.arguments
        userId = args?.getString(Constants().SELECTED_USER_ID_TAG)

        binding.tvTitle.text = selectedUser?.email
        binding.firstNameEditText.setText(selectedUser?.firstName)
        binding.lastNameEditText.setText(selectedUser?.lastName)
        binding.registerEmailEditText.setText(selectedUser?.email)
        binding.addressEditText.setText(selectedUser?.address)

        roleToUpdate = if (selectedUser?.role?.lowercase(Locale.ROOT) == Constants().DEFAULT_ROLE) {
            binding.userRoleDropdown.setSelection(1)
            Constants().ADMIN_ROLE
        } else {
            binding.userRoleDropdown.setSelection(0)
            Constants().DEFAULT_ROLE
        }
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                selectedUser = child.getValue(User::class.java)
                if (userId == selectedUser?.id) {
                    break
                }
            }

            setUI()
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    private fun setupListener() {
        binding.btnUpdateUser.setOnClickListener {
            if (checkFields()) {
                updateUser()
            }
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.dropdown_roles,
            android.R.layout.simple_spinner_item,
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.userRoleDropdown.adapter = adapter
        }
    }

    private fun checkFields(): Boolean {
        if (binding.firstNameEditText.text.toString() == "" ||
            binding.lastNameEditText.text.toString() == "" ||
            binding.registerEmailEditText.text.toString() == ""
        ) {
            Toast.makeText(requireContext(), "Invalid input!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun updateUser() {
        database.child("${userId}/email").setValue(binding.registerEmailEditText.text.toString())
        database.child("${userId}/firstName").setValue(binding.firstNameEditText.text.toString())
        database.child("${userId}/lastName").setValue(binding.lastNameEditText.text.toString())
        if (binding.addressEditText.text.toString() != "") {
            database.child("${userId}/address").setValue(binding.addressEditText.text.toString())
        }
        database.child("${userId}/role").setValue(roleToUpdate)
        findNavController().navigate(R.id.main_fragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    companion object {
        val TAG = UserDetailsFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): UserDetailsFragment {
            val args = Bundle()
            val fragment = UserDetailsFragment()
            fragment.arguments = args

            return fragment
        }
    }
}