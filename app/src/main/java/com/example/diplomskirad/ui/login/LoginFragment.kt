package com.example.diplomskirad.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.common.preferences.LoginSharedPreferences
import com.example.diplomskirad.databinding.FragmentLoginBinding
import com.example.diplomskirad.service_manager.user_manager.UserManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var sharedPreferences: LoginSharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        sharedPreferences = LoginSharedPreferences(requireContext())
        setupClickListeners()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    private fun setupClickListeners() {
        binding.btnSignup.setOnClickListener {
            if (checkInputFields()) {
                loginUser()
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.register_fragment)
        }
    }

    private fun checkInputFields(): Boolean {
        if (binding.loginEmail.text.toString() == "" || !binding.loginEmail.text.toString().contains("@")) {
            Toast.makeText(requireContext(), getString(R.string.invalid_email), Toast.LENGTH_SHORT).show()

            return false
        }

        if (binding.loginPassword.text.toString() == "" || binding.loginPassword.text.toString().length < 6) {
            Toast.makeText(requireContext(), getString(R.string.invalid_password), Toast.LENGTH_SHORT).show()

            return false
        }

        return true
    }

    private fun getUser(): String {

        return ""
    }

    private fun loginUser() {
        val role = getUser()

        auth.signInWithEmailAndPassword(binding.loginEmail.text.toString(), binding.loginPassword.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("provjera", "signInWithEmail:success ${auth.currentUser}")
                    UserManager().setUser(binding.loginEmail.toString(), Constants().DEFAULT_ROLE)
                    sharedPreferences?.saveEmail(binding.loginEmail.text.toString())
                    sharedPreferences?.saveRole(role)

                    findNavController().navigate(R.id.main_fragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("provjera", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = LoginFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): LoginFragment {
            val args = Bundle()
            val fragment = LoginFragment()
            fragment.arguments = args

            return fragment
        }
    }
}