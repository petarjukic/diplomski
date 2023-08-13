package com.example.diplomskirad.ui.cart_screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.databinding.FragmentCartBinding
import com.example.diplomskirad.eventbus.UpdateCartEvent
import com.example.diplomskirad.listener.ICartLoadListener
import com.example.diplomskirad.model.Cart
import com.example.diplomskirad.model.SoldItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CartFragment : Fragment(), ICartLoadListener {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var databaseItem: DatabaseReference
    private lateinit var listener: ICartLoadListener

    private var adapter: CartAdapter? = null
    private var cartList: MutableList<Cart> = ArrayList()
    private var soldItems: MutableList<SoldItems> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("cart")
        databaseItem = FirebaseDatabase.getInstance().getReference("sold_items")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        getProducts()
        setListeners()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    private fun setListeners() {
        listener = this

        if (auth.currentUser != null) {
            binding.btnCheckout.visibility = View.VISIBLE
            binding.btnCheckout.setOnClickListener {
                //TODO buy items
                updateItems()
            }
        } else {
            binding.btnCheckout.visibility = View.GONE
        }

        binding.btnHomeScreen.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateItems() {
        var counter = 0
        for (cartItem in cartList) {
            if (cartItem.id != null && cartItem.name != null) {
                for (item in soldItems) {
                    if (item.count != null) {
                        if (item.id?.equals(cartItem.id) == true) {
                            counter = item.count!!
                        }
                    }
                }
                //TODO update number of items from cart
//                databaseItem.child("${item.id}/${item.productName}").setValue("YourDateHere");
            }
        }
    }

    fun navigateToProductDetails(productId: String) {
        val bundle = bundleOf(Constants().SELECTED_PRODUCT_ID_TAG to productId)
        findNavController().navigate(R.id.productDetailsFragment, bundle)
    }

    private fun getProducts() {
        cartList.clear()
        val user = Firebase.auth.currentUser

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val cart = data.getValue(Cart::class.java)

                        if (cart != null && cart.userId.equals(user?.email)) {
                            cartList.add(cart)
                        }
                    }

                    listener.onLoadCartSuccess(cartList)
                } else {
                    listener.onLoadCartError("Unable to load items!")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("databaseError", error.message)
                listener.onLoadCartError(error.message)
            }
        })
    }

    override fun onLoadCartSuccess(cartList: List<Cart>) {
        adapter = CartAdapter(this, cartList)

        if (cartList.isEmpty()) {
            binding.emptyCartScreen.visibility = View.VISIBLE
            binding.cartScreen.visibility = View.GONE
        } else {
            binding.emptyCartScreen.visibility = View.GONE
            binding.cartScreen.visibility = View.VISIBLE

            val llm = LinearLayoutManager(requireContext())
            llm.orientation = LinearLayoutManager.VERTICAL
            binding.rvCart.layoutManager = llm
            binding.rvCart.adapter = adapter
            binding.rvCart.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

            countCartItems(cartList)
        }
    }

    private fun countCartItems(cartList: List<Cart>) {
        var sum = 0.0
        var totalItems = 0

        for (cart in cartList) {
            sum += cart.price!!
            totalItems += cart.quantity
        }

        binding.totalPriceValue.text = StringBuilder("$ ").append(sum)
        binding.totalItemsValue.text = totalItems.toString()
    }

    override fun onSuccessMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onLoadCartError(message: String?) {
        binding.emptyCartScreen.visibility = View.VISIBLE
        binding.cartScreen.visibility = View.GONE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onUpdateCartEvent(event: UpdateCartEvent) {
        getProducts()
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
        cartList.clear()
        adapter = null
        _binding = null
    }

    companion object {
        val TAG = CartFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): CartFragment {
            val args = Bundle()
            val fragment = CartFragment()
            fragment.arguments = args

            return fragment
        }
    }
}