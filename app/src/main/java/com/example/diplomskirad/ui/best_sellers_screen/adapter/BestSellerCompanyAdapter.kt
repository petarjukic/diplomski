package com.example.diplomskirad.ui.best_sellers_screen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.ui.best_sellers_screen.BestSellerCompaniesFragment

class BestSellerCompanyAdapter(
    private val companyList: List<String>,
    private val fragment: BestSellerCompaniesFragment
) : RecyclerView.Adapter<BestSellerCompanyAdapter.BestSellerCompanyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestSellerCompanyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.best_seller_genre_item, parent, false)

        return BestSellerCompanyHolder(view)
    }

    override fun getItemCount() = companyList.size

    override fun onBindViewHolder(holder: BestSellerCompanyHolder, position: Int) {
        holder.companyName.text = companyList[position]

        holder.companyItem.setOnClickListener {
            companyList[position].let { it1 -> fragment.navigateToCompanyItems(it1) }
        }
    }

    class BestSellerCompanyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val companyName: TextView
        val companyItem: View

        init {
            companyName = view.findViewById(R.id.category_name)
            companyItem = view.findViewById(R.id.category_item)
        }
    }
}