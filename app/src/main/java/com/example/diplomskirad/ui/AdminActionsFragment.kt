package com.example.diplomskirad.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomskirad.R
import com.example.diplomskirad.databinding.FragmentAdminActionsBinding

class AdminActionsFragment : Fragment() {
    private var _binding: FragmentAdminActionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminActionsBinding.inflate(inflater, container, false)

        setListener()

        return binding.root
    }

    private fun setListener() {
        binding.btnCategory.setOnClickListener {
            findNavController().navigate(R.id.category_list_fragment)
        }

        binding.btnUsers.setOnClickListener {
            findNavController().navigate(R.id.users_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = AdminActionsFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): AdminActionsFragment {
            val arg = Bundle()
            val fragment = AdminActionsFragment()
            fragment.arguments = arg

            return fragment
        }
    }
}