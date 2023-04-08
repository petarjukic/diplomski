package com.example.diplomskirad.ui.products.product_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.diplomskirad.databinding.FragmentProductDetailsBinding

class ProductDetailsFragment : Fragment() {
    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        val TAG = ProductDetailsFragment::class.java.simpleName

        fun newInstance(): ProductDetailsFragment {
            val args = Bundle()
            val fragment = ProductDetailsFragment()
            fragment.arguments = args

            return fragment
        }
    }
}