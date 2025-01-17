package com.example.diplomskirad.ui.favorites_screen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.HandlerCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.common.database.AppDatabase
import com.example.diplomskirad.databinding.FragmentFavoritesBinding
import com.example.diplomskirad.model.Product
import com.example.diplomskirad.model.favorites.Favorites
import com.example.diplomskirad.ui.utils.ItemDecoration
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var database: DatabaseReference
    private lateinit var favorites: List<Favorites>

    private var productList: MutableList<Product>? = null

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("product")
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "favorites"
        ).build()
        productList = java.util.ArrayList()

        getAllData()

        return binding.root
    }

    private fun getAllData() {
        executorService.execute {
            val favoriteDao = db.favoritesDao()
            favorites = favoriteDao.getAll()

            handler.post {
                database.addValueEventListener(postListener)
            }
        }
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val product = child.getValue<Product>()
                if (product != null && favorites.map { it.productId!! }.contains(product.id)) {
                    productList?.add(product)
                }
            }
            setUI()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("databaseError", databaseError.message)
            throw databaseError.toException()
        }
    }

    fun navigateToDetails(productId: String) {
        val bundle = bundleOf(Constants().SELECTED_PRODUCT_ID_TAG to productId)
        findNavController().navigate(R.id.productDetailsFragment, bundle)
    }

    private fun setUI() {
        val adapter = productList?.let { FavoritesAdapter(it, this) }
        binding.rvFavorite.layoutManager = GridLayoutManager(context, 2)
        binding.rvFavorite.adapter = adapter
        binding.rvFavorite.addItemDecoration(ItemDecoration())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        database.removeEventListener(postListener)
    }

    companion object {
        val TAG = FavoritesFragment::class.java

        @JvmStatic
        fun newInstance(): FavoritesFragment {
            val arg = Bundle()
            val fragment = FavoritesFragment()
            fragment.arguments = arg

            return fragment
        }
    }
}