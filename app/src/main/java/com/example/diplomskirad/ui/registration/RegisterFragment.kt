package com.example.diplomskirad.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomskirad.R
import com.example.diplomskirad.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        setupClickListeners()
        return binding.root
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            //TODO implement registration
            checkInputFields()
        }

        binding.registrationScreenAlreadyHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.goToLoginFragment)
        }
    }

    private fun checkInputFields() {

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    companion object {
        val TAG = RegisterFragment::class.java.simpleName

        fun newInstance(): RegisterFragment {
            val args = Bundle()
            val fragment = RegisterFragment()

            fragment.arguments = args

            return fragment
        }
    }
}