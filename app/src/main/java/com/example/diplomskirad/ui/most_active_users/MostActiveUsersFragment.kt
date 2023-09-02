package com.example.diplomskirad.ui.most_active_users

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.databinding.FragmentMostActiveUsersBinding
import com.example.diplomskirad.model.SoldItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class MostActiveUsersFragment : Fragment() {
    private var _binding: FragmentMostActiveUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference

    private var adapter: MostActiveUsersAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("soldItems")
        _binding = FragmentMostActiveUsersBinding.inflate(inflater, container, false)

        database.addValueEventListener(postListener)

        return binding.root
    }

    private val postListener = object : ValueEventListener {
        val soldItems: MutableList<SoldItems> = mutableListOf()
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val item = child.getValue<SoldItems>()

                if (item != null) {
                    soldItems.add(item)
                }
            }
            setAdapter(soldItems)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("databaseError", databaseError.message)
        }
    }

    private fun setAdapter(soldItems: List<SoldItems>) {
        binding.loading.visibility = View.GONE

        val sortedUsers = findUsers(soldItems)
        adapter = MostActiveUsersAdapter(sortedUsers, this)
        val llm = LinearLayoutManager(requireContext())
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rcMostActiveUsers.layoutManager = llm
        binding.rcMostActiveUsers.adapter = adapter
    }

    private fun findUsers(soldItems: List<SoldItems>): List<String> {
        val sortedList = hashMapOf<String, Int>()

        for (item in soldItems) {
            if (!sortedList.keys.contains(item.userId)) {
                sortedList[item.userId!!] = item.count!!
            } else {
                sortedList[item.userId!!] = sortedList.getValue(item.userId) + item.count!!
            }
        }

        val sortedMap = sortedList.toList().sortedBy { (_, v) -> v }.reversed().toMap()
        return sortedMap.keys.toList()
    }

    fun navigateUserDetails(userId: String) {
        Log.d("provjera", "AAAAAAAA $userId")
        val bundle = bundleOf(Constants().SELECTED_USER_ID_TAG to userId)
        findNavController().navigate(R.id.user_details_fragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(postListener)
        adapter = null
        _binding = null
    }

    companion object {
        val TAG = MostActiveUsersFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): MostActiveUsersFragment {
            val args = Bundle()
            val fragment = MostActiveUsersFragment()
            fragment.arguments = args

            return fragment
        }
    }
}