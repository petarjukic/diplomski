package com.example.diplomskirad.ui.home_screen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.MainActivity
import com.example.diplomskirad.R
import com.example.diplomskirad.common.preferences.LoginSharedPreferences
import com.example.diplomskirad.databinding.FragmentMainBinding
import com.example.diplomskirad.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var isSignedIn: Boolean = false
    private lateinit var auth: FirebaseAuth
    private var sharedPreferences: LoginSharedPreferences? = null
    private var productList: MutableList<Product>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        sharedPreferences = LoginSharedPreferences(requireContext())

        checkIsUserSignedIn()
        setUI()
        setupListener()

        return binding.root
    }

    private fun setUI() {
        val userList: MutableList<String> = ArrayList()
        val productAdapter = productList?.let { ProductAdapter(it) }

        val layout = LinearLayoutManager(requireContext())
        layout.orientation = LinearLayoutManager.VERTICAL
        binding.rvProduct.layoutManager = layout
        binding.rvProduct.adapter = productAdapter

        if (!isSignedIn) {
            userList.add("Login")
            userList.add("Register")
        } else {
            userList.add("Logout")
            userList.add("Admin actions")
        }

        userList.add("Profile")

        val adapter = UserActionsAdapter(userList, this)
        val llm = LinearLayoutManager(requireContext())
        llm.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvUserActions.layoutManager = llm
        binding.rvUserActions.adapter = adapter
    }

    fun logoutUser() {
        if (isSignedIn) {
            FirebaseAuth.getInstance().signOut()
            requireActivity().finish()
            requireActivity().startActivity(Intent(context, MainActivity::class.java))
            requireActivity().finishAffinity()
        } else {
            findNavController().navigate(R.id.login_fragment)
        }
    }

    fun navigateToUserActions() {
        if (isSignedIn) {
            findNavController().navigate(R.id.admin_actions_fragment)
        } else {
            findNavController().navigate(R.id.register_fragment)
        }
    }

    private fun setupListener() {
        binding.shoppingCart.setOnClickListener {
            findNavController().navigate(R.id.cartFragment)
        }

//        binding.login.setOnClickListener {
//            findNavController().navigate(R.id.login_fragment)
//        }
//
//        binding.userList.setOnClickListener {
//            findNavController().navigate(R.id.admin_actions_fragment)
//        }
//
//        binding.logout.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            requireActivity().finish()
//            requireActivity().startActivity(Intent(context, MainActivity::class.java))
//            requireActivity().finishAffinity()
//        }
    }

    private fun checkIsUserSignedIn() {
        val user = Firebase.auth.currentUser
        isSignedIn = user != null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = MainFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): MainFragment {
            val args = Bundle()
            val fragment = MainFragment()
            fragment.arguments = args

            return fragment
        }
    }
}