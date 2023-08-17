package com.example.diplomskirad.ui.base_bottom_sheet.company

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.databinding.BottomSheetItemBinding

class CompanyBottomSheetAdapter(
    private val dataList: MutableList<String>,
    private val fragment: CompanyBottomSheetFragment
) :
    RecyclerView.Adapter<CompanyBottomSheetAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ItemViewHolder(BottomSheetItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.title.text = dataList[position]

        holder.binding.root.setOnClickListener {
            fragment.itemClicked(dataList[position], holder.binding.title)
        }
    }

    override fun getItemCount() = dataList.size

    class ItemViewHolder(val binding: BottomSheetItemBinding) : RecyclerView.ViewHolder(binding.root)
}