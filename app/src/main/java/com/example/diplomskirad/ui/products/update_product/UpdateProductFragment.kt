package com.example.diplomskirad.ui.products.update_product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.diplomskirad.databinding.FragmentUpdateProductBinding

class UpdateProductFragment : Fragment() {
    private var _binding: FragmentUpdateProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateProductBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = UpdateProductFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): UpdateProductFragment {
            val args = Bundle()
            val fragment = UpdateProductFragment()
            fragment.arguments = args

            return fragment
        }
    }
}
