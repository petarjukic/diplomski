package com.example.diplomskirad.ui.user.user_details

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
import com.example.diplomskirad.service_manager.user_manager.UserManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale


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
        val args = this.arguments
        userId = args?.getString(Constants().SELECTED_USER_ID_TAG)
        database.addValueEventListener(postListener)

        setupListener()

        return binding.root
    }

    private fun setUI() {
        binding.tvTitle.text = selectedUser?.email
        binding.firstNameEditText.setText(selectedUser?.firstName)
        binding.lastNameEditText.setText(selectedUser?.lastName)
        binding.emailEditText.setText(selectedUser?.email)
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
            binding.emailEditText.text.toString() == ""
        ) {
            Toast.makeText(requireContext(), "Invalid input!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun updateUser() {
        roleToUpdate = binding.userRoleDropdown.selectedItem.toString()
        database.child("${userId}/email").setValue(binding.emailEditText.text.toString())
        database.child("${userId}/firstName").setValue(binding.firstNameEditText.text.toString())
        database.child("${userId}/lastName").setValue(binding.lastNameEditText.text.toString())
        if (binding.addressEditText.text.toString() != "") {
            database.child("${userId}/address").setValue(binding.addressEditText.text.toString())
        }
        database.child("${userId}/role").setValue(roleToUpdate)
        if (roleToUpdate != null) {
            UserManager().setUser(binding.emailEditText.text.toString(), roleToUpdate!!)
        } else {
            UserManager().setUser(binding.emailEditText.text.toString(), Constants().DEFAULT_ROLE)
        }

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(postListener)
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