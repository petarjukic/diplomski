package com.example.diplomskirad.ui.base_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.R
import com.example.diplomskirad.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var adapter: BottomSheetAdapter? = null
    private var listener: BottomSheetListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)

        adapter = BottomSheetAdapter(dataList, this)
        val llm = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(context, llm.orientation)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rvBottomSheet.layoutManager = llm
        binding.rvBottomSheet.addItemDecoration(dividerItemDecoration)
        binding.rvBottomSheet.adapter = adapter

        return binding.root
    }

    fun itemClicked(selectedItem: String, title: TextView) {
        if (selectedData?.lowercase() == selectedItem.lowercase()) {
            title.setTextColor(resources.getColor(R.color.black))
        } else {
            for (data in dataList) {
                if (data.lowercase() == selectedItem.lowercase()) {
                    title.setTextColor(resources.getColor(R.color.checked_item))
                    listener?.onCategoryItemClicked(data)
                    break
                }
            }
        }
    }

    fun setListener(listener: BottomSheetListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }

    companion object {
        val TAG = BottomSheetFragment::class.java.simpleName
        private var dataList: MutableList<String> = mutableListOf()
        private var selectedData: String? = null

        @JvmStatic
        fun newInstance(dataList: MutableList<String>, selectedData: String?): BottomSheetFragment {
            this.dataList = dataList
            this.selectedData = selectedData
            val args = Bundle()
            val fragment = BottomSheetFragment()
            fragment.arguments = args

            return fragment
        }
    }

    interface BottomSheetListener {
        fun onCategoryItemClicked(title: String)
    }
}