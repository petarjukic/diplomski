package com.example.diplomskirad.ui.base_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var adapter: BottomSheetAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)

        adapter = BottomSheetAdapter(dataList)
        val llm = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(context, llm.orientation)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rvBottomSheet.layoutManager = llm
        binding.rvBottomSheet.addItemDecoration(dividerItemDecoration)
        binding.rvBottomSheet.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }

    companion object {
        val TAG = BottomSheetFragment::class.java.simpleName
        private var dataList: MutableList<String> = mutableListOf()

        @JvmStatic
        fun newInstance(dataList: MutableList<String>): BottomSheetFragment {
            this.dataList = dataList
            val args = Bundle()
            val fragment = BottomSheetFragment()
            fragment.arguments = args

            return fragment
        }
    }
}