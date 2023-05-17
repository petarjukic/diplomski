package com.example.diplomskirad.ui.user.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomskirad.MainActivity
import com.example.diplomskirad.R
import com.example.diplomskirad.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var signedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        if (FirebaseAuth.getInstance().currentUser != null) {
            signedIn = true
        }
        setUI()
        setListener()
        return binding.root
    }

    private fun setUI() {
        if (signedIn) {
            binding.userEmail.visibility = View.VISIBLE
            binding.userEmail.text = auth.currentUser?.email
            binding.logoutUser.visibility = View.VISIBLE
            binding.loginUser.visibility = View.GONE
            binding.registerUser.visibility = View.GONE
            binding.changePassword.visibility = View.VISIBLE
        } else {
            binding.userEmail.visibility = View.GONE
            binding.logoutUser.visibility = View.GONE
            binding.loginUser.visibility = View.VISIBLE
            binding.registerUser.visibility = View.VISIBLE
            binding.changePassword.visibility = View.GONE
        }
    }

    private fun setListener() {
        binding.logoutUser.setOnClickListener {
            logoutUser()
        }
        binding.loginUser.setOnClickListener {
            findNavController().navigate(R.id.login_fragment)
        }
        binding.registerUser.setOnClickListener {
            findNavController().navigate(R.id.register_fragment)
        }
        binding.changePassword.setOnClickListener {
//            findNavController().navigate()
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        requireActivity().finish()
        requireActivity().startActivity(Intent(context, MainActivity::class.java))
        requireActivity().finishAffinity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = ProfileFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): ProfileFragment {
            val args = Bundle()
            val fragment = ProfileFragment()
            fragment.arguments = args

            return fragment
        }
    }
}