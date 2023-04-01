package com.example.diplomskirad.ui.users.user_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomskirad.R
import com.example.diplomskirad.databinding.FragmentUserDetailsBinding
import com.example.diplomskirad.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue


class UserDetailsFragment : Fragment() {
    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        setUI()
        setupListener()

        return binding.root
    }

    private fun setUI() {
        binding.tvTitle.text = "User E-mail"
        binding.firstNameEditText.setText("First name")
        binding.lastNameEditText.setText("Last name")
        binding.registerEmailEditText.setText("email")
        binding.addressEditText.setText("Address")

    }

    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            val post = dataSnapshot.getValue<User>()
            // ...
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.d("provjera", "loadPost:onCancelled", databaseError.toException())
        }
    }
//    postReference.addValueEventListener(postListener)

    private fun setupListener() {
        binding.btnUpdateUser.setOnClickListener {
            if (checkFields()) {
                updateUser()
                findNavController().navigate(R.id.main_fragment)

            }
        }
    }

    private fun checkFields(): Boolean {

        return true
    }

    private fun updateUser() {

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    companion object {
        val TAG = UserDetailsFragment::class.java.simpleName

        fun newInstance(): UserDetailsFragment {
            val args = Bundle()
            val fragment = UserDetailsFragment()

            fragment.arguments = args

            return fragment
        }
    }
}