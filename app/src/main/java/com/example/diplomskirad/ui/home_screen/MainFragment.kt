package com.example.diplomskirad.ui.home_screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.diplomskirad.ui.search.SearchActivity
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

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var isSignedIn: Boolean = false
    private var sharedPreferences: LoginSharedPreferences? = null
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
        val user = Firebase.auth.currentUser

        FirebaseDatabase.getInstance().getReference("cart")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cart in snapshot.children) {
                        val cartModel = cart.getValue(Cart::class.java)

                        if (cartModel != null && cartModel.userId.equals(user?.email)) {
                            cartData.add(cartModel)
                        }
                    }
                    cartListener?.onLoadCartSuccess(cartData)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("databaseError", error.message)
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
            Log.e("databaseError", databaseError.message)
            throw databaseError.toException()
        }
    }

    private fun setUserList(): List<String> {
        val userList: MutableList<String> = ArrayList()

        if (!isSignedIn) {
            userList.add("Login")
            userList.add("Register")
        } else {
            userList.add("Logout")
            if (sharedPreferences?.getRole() == Constants().ADMIN_ROLE) {
                userList.add("Admin actions")
            }
        }

        userList.add("Profile")
        userList.add("Best seller")
        userList.add("Best seller genre")
        userList.add("Companies")

        return userList
    }

    private fun setUI() {
        val categories = categoryList?.distinct()
        productList?.add(Product("dsa2", "productName"))
        val productAdapter = productList?.let { ProductAdapter(it, this) }
        categoryAdapter = categories?.let { CategoryAdapter(it, this) }

        binding.rvProduct.layoutManager = GridLayoutManager(context, 2)
        binding.rvProduct.adapter = productAdapter
        binding.rvProduct.addItemDecoration(ItemDecoration())

        val adapter = UserActionsAdapter(setUserList(), this)
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
            sharedPreferences?.deleteLoginInfoForUser(sharedPreferences?.getEmail())
            sharedPreferences?.deleteRoleInfoForUser(sharedPreferences?.getRole())

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

    fun gotoBestSellerScreen() {
        findNavController().navigate(R.id.bestSellersFragment)
    }

    fun goToBesSellerGenreScreen() {
        findNavController().navigate(R.id.bestSellerGenresFragment)
    }

    fun goToCompaniesScreen() {
        findNavController().navigate(R.id.companiesFragment)
    }

    fun navigateToUserActions() {
        if (isSignedIn && sharedPreferences?.getRole() == Constants().ADMIN_ROLE) {
            findNavController().navigate(R.id.admin_actions_fragment)
        } else if (isSignedIn && sharedPreferences?.getRole() == Constants().DEFAULT_ROLE) {
            findNavController().navigate(R.id.profileFragment)
        } else {
            findNavController().navigate(R.id.register_fragment)
        }
    }

    private fun setupListener() {
        binding.shoppingCart.setOnClickListener {
            findNavController().navigate(R.id.cartFragment)
        }

        binding.searchView.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            activity?.startActivity(intent)
        }
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