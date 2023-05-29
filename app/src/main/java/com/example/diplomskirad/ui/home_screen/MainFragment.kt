package com.example.diplomskirad.ui.home_screen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
    private var displayList: MutableList<Product>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.menu_search)

        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        displayList?.clear()



                        binding.rvProduct.adapter?.notifyDataSetChanged()
                    } else {
                        displayList?.clear()
                        productList?.let { displayList?.addAll(it) }
                        binding.rvProduct.adapter?.notifyDataSetChanged()
                    }
                    // on below line we are checking
                    // if query exist or not.
//                if (programmingLanguagesList.contains(query)) {
//                    // if query exist within list we
//                    // are filtering our list adapter.
//                    listAdapter.filter.filter(query)
//                } else {
//                    // if query is not present we are displaying
//                    // a toast message as no  data found..
//                    Toast.makeText(this@MainActivity, "No Language found..", Toast.LENGTH_LONG)
//                        .show()
//                }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // if query text is change in that case we
                    // are filtering our adapter with
                    // new text on below line.
//                listAdapter.filter.filter(newText)
                    return true
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        sharedPreferences = LoginSharedPreferences(requireContext())
        productList = java.util.ArrayList()
        displayList = java.util.ArrayList()

        checkIsUserSignedIn()
        setUI()
        setupListener()

        return binding.root
    }

    private fun setUI() {
        val userList: MutableList<String> = ArrayList()
        productList?.add(Product("dsa2", "productName"))
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

    fun goToProfileScreen() {
        findNavController().navigate(R.id.profileFragment)
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

//        binding.searchView.setOnClickListener {
//            findNavController().navigate(R.id.searchFragment)
//        }

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