package com.example.diplomskirad.ui.cart_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.databinding.FragmentCartBinding
import com.example.diplomskirad.listener.IProductLoadListener
import com.example.diplomskirad.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class CartFragment : Fragment(), IProductLoadListener {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var adapter: CartAdapter? = null
    private lateinit var listener: IProductLoadListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("cart")
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

    private fun setListeners() {
        listener = this

        binding.btnCheckout.setOnClickListener {
            // TODO buy items
        }

        binding.btnHomeScreen.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getProducts() {
        val cartList: MutableList<Product> = ArrayList()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val product = data.getValue(Product::class.java)
                        if (product != null) {
                            cartList.add(product)
                        }
                    }
                    listener.onSuccess(cartList)
                } else {
                    listener.onError("Unable to load items!")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onError(error.message)
            }
        })
    }

    override fun onSuccess(cartList: List<Product>) {
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
        }
    }

    override fun onError(message: String?) {
        binding.emptyCartScreen.visibility = View.VISIBLE
        binding.cartScreen.visibility = View.GONE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        database.removeEventListener(postListener)
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