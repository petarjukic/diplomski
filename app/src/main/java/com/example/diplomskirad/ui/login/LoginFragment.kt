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
import com.example.diplomskirad.model.User
import com.example.diplomskirad.service_manager.user_manager.UserManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var sharedPreferences: LoginSharedPreferences? = null
    private lateinit var database: DatabaseReference
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("users")
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
                database.addValueEventListener(postListener)
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.register_fragment)
        }
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val currentUser = child.getValue<User>()
                if (currentUser != null) {
                    if (currentUser.email?.lowercase().equals(binding.loginEmail.text.toString().lowercase())) {
                        user = User(email = currentUser.email, role = currentUser.role)
                        loginUser()
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("databaseError", databaseError.message)
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

    private fun getUserRole(): String? {
        return user.role
    }

    private fun loginUser() {
        auth.signInWithEmailAndPassword(binding.loginEmail.text.toString(), binding.loginPassword.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val role = getUserRole()

                    Log.d("provjera", "signInWithEmail:success ${auth.currentUser}")
                    if (role != null) {
                        UserManager().setUser(binding.loginEmail.toString(), role)
                        sharedPreferences?.saveRole(role)
                    } else {
                        UserManager().setUser(binding.loginEmail.toString(), Constants().DEFAULT_ROLE)
                        sharedPreferences?.saveRole(Constants().DEFAULT_ROLE)
                    }

                    sharedPreferences?.saveEmail(binding.loginEmail.text.toString())

                    findNavController().navigate(R.id.main_fragment)
                } else {
                    Log.d("databaseError", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(postListener)
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