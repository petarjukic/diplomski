package com.example.diplomskirad.ui.registration

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
import com.example.diplomskirad.databinding.FragmentRegisterBinding
import com.example.diplomskirad.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        database = Firebase.database.reference
    }

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
            if (checkInputFields()) {
                // TODO register user into firebase and create user
                findNavController().navigate(R.id.login_fragment)
//                registerUser()
            }
        }

        binding.registrationScreenAlreadyHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.login_fragment)
        }
    }

    private fun registerUser() {
        auth.createUserWithEmailAndPassword(
            binding.registerEmailEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("provjera", "createUserWithEmail:success auth.currentUser ${auth.currentUser}")
                    Toast.makeText(requireContext(), "Success register", Toast.LENGTH_SHORT).show()

                    val uuid = UUID.randomUUID().toString()
                    val user = User(
                        uuid,
                        binding.firstNameEditText.text.toString(),
                        binding.lastNameEditText.text.toString(),
                        binding.registerEmailEditText.text.toString(),
                        binding.passwordEditText.text.toString(),
                        null,
                        Constants().DEFAULT_ROLE
                    )
                    database.child("users").child(uuid).setValue(user)
//                    val user = auth.currentUser
                    findNavController().navigate(R.id.login_fragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("provjera", "createUserWithEmail:failure")
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun checkInputFields(): Boolean {
        if (binding.firstNameEditText.text.toString() != "") {
            Toast.makeText(requireContext(), getString(R.string.invalid_first_name), Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.lastNameEditText.text.toString() != "") {
            Toast.makeText(requireContext(), getString(R.string.invalid_last_name), Toast.LENGTH_SHORT).show()
            return false
        }

        if (!binding.registerEmailEditText.text.toString().contains("@")) {
            Toast.makeText(requireContext(), getString(R.string.invalid_email), Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.passwordEditText.text.toString() != binding.confirmPasswordEditText.text.toString()
            || binding.passwordEditText.text.toString().length < 6 || binding.passwordEditText.text == null
            || binding.confirmPasswordEditText.text == null
        ) {
            Toast.makeText(requireContext(), getString(R.string.invalid_password), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
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