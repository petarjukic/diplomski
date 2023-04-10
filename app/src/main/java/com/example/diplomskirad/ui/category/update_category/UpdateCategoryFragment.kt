package com.example.diplomskirad.ui.category.update_category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.diplomskirad.databinding.FragmentUpdateCategoryBinding

class UpdateCategoryFragment : Fragment() {
    private var _binding: FragmentUpdateCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateCategoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        val TAG = UpdateCategoryFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): UpdateCategoryFragment {
            val args = Bundle()
            val fragment = UpdateCategoryFragment()
            fragment.arguments = args

            return fragment
        }
    }
}