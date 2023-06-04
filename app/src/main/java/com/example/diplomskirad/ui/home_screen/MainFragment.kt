package com.example.diplomskirad.ui.home_screen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.MainActivity
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.common.preferences.LoginSharedPreferences
import com.example.diplomskirad.databinding.FragmentMainBinding
import com.example.diplomskirad.eventbus.UpdateCartEvent
import com.example.diplomskirad.listener.ICartLoadListener
import com.example.diplomskirad.model.Cart
import com.example.diplomskirad.model.Product
import com.example.diplomskirad.ui.utils.ItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainFragment : Fragment(), ICartLoadListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var isSignedIn: Boolean = false
    private lateinit var auth: FirebaseAuth
    private var sharedPreferences: LoginSharedPreferences? = null
    private lateinit var database: DatabaseReference
    private var productList: MutableList<Product>? = null
    private var displayList: MutableList<Product>? = null
    private var categoryList: MutableList<String>? = null
    private var categoryAdapter: CategoryAdapter? = null
    private var cartListener: ICartLoadListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("product")
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
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
        categoryList = java.util.ArrayList()
        displayList = java.util.ArrayList()
        cartListener = this

        checkIsUserSignedIn()
        getData()
        setupListener()
        countDataFromDatabase()

        return binding.root
    }

    private fun countDataFromDatabase() {
        val cartData: MutableList<Cart> = ArrayList()
        auth.currentUser?.uid?.let { FirebaseDatabase.getInstance().getReference("cart").child(it) }
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cart in snapshot.children) {
                        val cartModel = cart.getValue(Cart::class.java)
                        if (cartModel != null) {
                            cartData.add(cartModel)
                        }
                    }
                    cartListener?.onLoadCartSuccess(cartData)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener?.onLoadCartError(error.message)
                }
            })
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val product = child.getValue<Product>()
                if (product != null) {
                    productList?.add(product)
                    product.categoryId?.let { categoryList?.add(it) }
                }
            }
            setUI()
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    private fun setUI() {
        val categories = categoryList?.distinct()
        val userList: MutableList<String> = ArrayList()
        productList?.add(Product("dsa2", "productName"))
        val productAdapter = productList?.let { ProductAdapter(it, this) }
        categoryAdapter = categories?.let { CategoryAdapter(it, this) }

        binding.rvProduct.layoutManager = GridLayoutManager(context, 2)
        binding.rvProduct.adapter = productAdapter
        binding.rvProduct.addItemDecoration(ItemDecoration())

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

        val categoryLayout = LinearLayoutManager(requireContext())
        categoryLayout.orientation = LinearLayoutManager.HORIZONTAL
        binding.categories.layoutManager = categoryLayout
        binding.categories.adapter = categoryAdapter
    }

    private fun getData() {
        database.addValueEventListener(postListener)
    }

    fun navigateToDetails(productId: String) {
        val bundle = bundleOf(Constants().SELECTED_PRODUCT_ID_TAG to productId)
        findNavController().navigate(R.id.productDetailsFragment, bundle)
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

    fun navigateToFiltered(category: String) {
        val bundle = bundleOf(Constants().FILTER_CATEGORY to category)
        findNavController().navigate(R.id.filteredItemsFragment, bundle)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onUpdateCartEvent(event: UpdateCartEvent) {
        countDataFromDatabase()
    }

    override fun onStop() {
        super.onStop()

        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java)) {
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        }
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(postListener)
        categoryAdapter = null
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

    override fun onLoadCartSuccess(cartList: List<Cart>) {
        var cartSum = 0
        for (cart in cartList) {
            cartSum += cart.quantity
        }
        binding.badge.setNumber(cartSum)
    }

    override fun onSuccessMessage(message: String) {
        TODO("Not yet implemented")
    }

    override fun onLoadCartError(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
}