package com.example.diplomskirad.ui.base_bottom_sheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomskirad.databinding.BottomSheetItemBinding

class BottomSheetAdapter(private val dataList: MutableList<String>) :
    RecyclerView.Adapter<BottomSheetAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ItemViewHolder(BottomSheetItemBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.title.text = dataList[position]
    }

    class ItemViewHolder(val binding: BottomSheetItemBinding) : RecyclerView.ViewHolder(binding.root)

}