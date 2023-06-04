package com.example.diplomskirad

import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.databinding.ActivityMainBinding
import com.example.diplomskirad.model.Product

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private var adapter: SearchViewAdapter? = null
    private var productList: MutableList<Product> = ArrayList()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getData()
        createAdapter()
        menuInflater.inflate(R.menu.main, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                adapter.filter.filter(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun getData() {

    }

    private fun createAdapter() {
        val llm = LinearLayoutManager(baseContext)
        adapter = SearchViewAdapter(productList)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rvSearch.layoutManager = llm
        binding.rvSearch.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
    }
}