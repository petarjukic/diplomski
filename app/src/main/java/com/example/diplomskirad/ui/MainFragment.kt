package com.example.diplomskirad.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomskirad.R
import com.example.diplomskirad.databinding.FragmentMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var isSignedIn: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        checkIsUserSignedIn()
        setUI()
        setupListener()

        return binding.root
    }

    private fun setUI() {
        if (isSignedIn) {
            binding.login.visibility = View.GONE
        } else {
            binding.login.visibility = View.VISIBLE

        }
    }

    private fun setupListener() {
        binding.login.setOnClickListener {
            findNavController().navigate(R.id.goToLoginFragment)
        }
    }

    private fun checkIsUserSignedIn() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in
            isSignedIn = true
        } else {
            // No user is signed in
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    companion object {
        val TAG = MainFragment::class.java.simpleName

        fun newInstance(): MainFragment {
            val args = Bundle()
            val fragment = MainFragment()
            fragment.arguments = args

            return fragment
        }
    }
}