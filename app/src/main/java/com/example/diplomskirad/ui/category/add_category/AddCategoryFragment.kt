package com.example.diplomskirad.ui.category.add_category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.diplomskirad.databinding.FragmentAddCategoryBinding

class AddCategoryFragment : Fragment() {
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        val TAG = AddCategoryFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): AddCategoryFragment {
            val args = Bundle()
            val fragment = AddCategoryFragment()
            fragment.arguments = args

            return fragment
        }
    }
}