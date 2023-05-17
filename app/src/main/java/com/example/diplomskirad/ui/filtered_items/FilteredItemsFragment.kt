package com.example.diplomskirad.ui.filtered_items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.diplomskirad.databinding.FragmentFilteredItemsBinding

class FilteredItemsFragment : Fragment() {
    private var _binding: FragmentFilteredItemsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilteredItemsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = FilteredItemsFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): FilteredItemsFragment {
            val args = Bundle()
            val fragment = FilteredItemsFragment()
            fragment.arguments = args

            return fragment
        }
    }
}