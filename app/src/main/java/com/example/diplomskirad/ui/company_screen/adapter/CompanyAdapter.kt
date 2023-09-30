package com.example.diplomskirad.ui.company_screen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.model.Company
import com.example.diplomskirad.ui.company_screen.CompaniesFragment

class CompanyAdapter(
    private val companies: List<Company>,
    private val fragment: CompaniesFragment,
    private val isAdmin: Boolean
) :
    RecyclerView.Adapter<CompanyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = companies.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.tvCompanyName.text = companies[position].companyName
        holder.tvCompanyName.setOnClickListener {
            val bundle = bundleOf(Constants().SELECTED_CATEGORY_ID to companies[position].id)
            fragment.navigateToDetails(bundle)
        }

        if (!isAdmin) {
            holder.btnRemoveItem.visibility = View.GONE
        } else {
            holder.btnRemoveItem.setOnClickListener {
                companies[position].id?.let { it1 -> fragment.removeCompany(it1) }
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCompanyName: TextView
        val btnRemoveItem: Button

        init {
            tvCompanyName = view.findViewById(R.id.category)
            btnRemoveItem = view.findViewById(R.id.remove_category)
        }
    }
}