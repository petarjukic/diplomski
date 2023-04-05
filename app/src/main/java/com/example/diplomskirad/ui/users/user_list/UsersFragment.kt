package com.example.diplomskirad.ui.users.user_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.diplomskirad.databinding.FragmentUsersBinding

class UsersFragment : Fragment() {
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    companion object {
        val TAG = UsersFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): UsersFragment {
            val args = Bundle()
            val fragment = UsersFragment()
            fragment.arguments = args

            return fragment
        }
    }
}