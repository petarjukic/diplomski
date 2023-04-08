package com.example.diplomskirad.ui.products.add_product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.diplomskirad.databinding.FragmentAddProductBinding

class AddProductFragment : Fragment() {
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        val TAG = AddProductFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): AddProductFragment {
            val args = Bundle()
            val fragment = AddProductFragment()
            fragment.arguments = args

            return fragment
        }
    }
}