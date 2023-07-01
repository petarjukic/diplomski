package com.example.diplomskirad.ui.search

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.R
import com.example.diplomskirad.databinding.ActivitySearchBinding
import com.example.diplomskirad.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    private lateinit var database: DatabaseReference
    private var productList: ArrayList<Product> = ArrayList()
    private var listAdapter: SearchAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("product")
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //TODO implement clickListener on search item
        database.addValueEventListener(postListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        val searchItem: MenuItem = menu.findItem(R.id.actionSearch)
        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filter(newText)
                }
                return false
            }
        })
        return true
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<Product> = ArrayList()

        for (item in productList) {
            if (item.productName?.lowercase()?.contains(text.lowercase()) == true) {
                filteredList.add(item)
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            listAdapter?.filterList(filteredList)
        }
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val product = child.getValue<Product>()
                if (product != null) {
                    productList.add(product)
                }
            }
            listAdapter?.notifyDataSetChanged()
            setData()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("databaseError", databaseError.message)
            throw databaseError.toException()
        }
    }

    private fun setData() {
        listAdapter = SearchAdapter(productList = productList, this)

        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rvSearchProducts.layoutManager = llm
        binding.rvSearchProducts.adapter = listAdapter
    }

    fun navigateToProductDetails(productId: String?) {
        val fragment = SearchFragment.newInstance()

//        fragment.navigate(productId)
    }

    override fun onDestroy() {
        super.onDestroy()
        database.removeEventListener(postListener)
    }
}