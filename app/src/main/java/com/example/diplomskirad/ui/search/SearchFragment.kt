//package com.example.diplomskirad.ui.search
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import android.widget.SearchView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import com.example.diplomskirad.R
//import com.example.diplomskirad.databinding.ActivityMainBinding
//import com.example.diplomskirad.databinding.FragmentSearchBinding
//import com.example.diplomskirad.model.Product
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.ktx.getValue
//
//class SearchFragment : AppCompatActivity() {
//    private lateinit var binding: FragmentSearchBinding
//
//    private lateinit var database: DatabaseReference
//    private var productList: ArrayList<Product> = ArrayList()
//    private var listAdapter: SearchAdapter? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        database = FirebaseDatabase.getInstance().getReference("product")
//
//        binding = FragmentSearchBinding.inflate(layoutInflater)
//
//        database.addValueEventListener(postListener)
//
//        listAdapter = SearchAdapter(productList = productList)
//        binding.idRVCourses.adapter = listAdapter
//
//        Log.d("provjera", "AAAAAAAA ${productList.size}")
//
////        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
////            override fun onQueryTextSubmit(query: String?): Boolean {
////                if(productList.contains(query)) {
////                    listAdapter.filter.filter(query)
////                }
////                return false
////            }
////
////            override fun onQueryTextChange(newText: String?): Boolean {
////                listAdapter.filter.filter(newText)
////                return false
////            }
////        })
//
//    }
//
////    override fun onCreateView(
////        inflater: LayoutInflater, container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View {
////
////    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        super.onCreateOptionsMenu(menu)
//
//        val inflater = menuInflater
//        inflater.inflate(R.menu.main, menu)
//        val searchItem: MenuItem = menu.findItem(R.id.actionSearch)
//        val searchView: SearchView = searchItem.actionView as SearchView
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText != null) {
//                    filter(newText)
//                }
//                return false
//            }
//        })
//        return true
//    }
//
//    private fun filter(text: String) {
//        val filteredList: ArrayList<Product> = ArrayList()
//
//        for (item in productList) {
//            if (item.productName?.lowercase()?.contains(text.lowercase()) == true) {
//                filteredList.add(item)
//            }
//        }
//        if (filteredList.isEmpty()) {
//            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
//        } else {
//            listAdapter?.filterList(filteredList)
//        }
//    }
//
//    private val postListener = object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            for (child in dataSnapshot.children) {
//                val product = child.getValue<Product>()
//                if (product != null) {
//                    productList.add(product)
//                }
//            }
//            listAdapter?.notifyDataSetChanged()
//        }
//
//        override fun onCancelled(databaseError: DatabaseError) {
//            Log.e("databaseError", databaseError.message)
//            throw databaseError.toException()
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        database.removeEventListener(postListener)
//    }
//}