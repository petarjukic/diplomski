package com.example.diplomskirad.ui.statistics.inactive_users

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.databinding.FragmentInactiveUsersBinding
import com.example.diplomskirad.model.SoldItems
import com.example.diplomskirad.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class InactiveUsersFragment : Fragment() {
    private var _binding: FragmentInactiveUsersBinding? = null
    private val binding get() = _binding!!

    private var adapter: InactiveUsersAdapter? = null

    private lateinit var database: DatabaseReference
    private lateinit var usersDatabaseReference: DatabaseReference

    private var soldItems: MutableList<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("soldItems")
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("users")

        _binding = FragmentInactiveUsersBinding.inflate(inflater, container, false)

        database.addValueEventListener(postListener)
        soldItems = mutableListOf()

        return binding.root
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val item = child.getValue<SoldItems>()

                if (item != null) {
                    item.userId?.let { soldItems?.add(it.lowercase()) }
                }
            }
            usersDatabaseReference.addValueEventListener(userPostListener)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("databaseError", databaseError.message)
        }
    }

    private val userPostListener = object : ValueEventListener {
        val users: MutableList<User> = mutableListOf()

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val item = child.getValue<User>()

                if (item != null) {
                    users.add(item)
                }
            }
            handleData(users)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("databaseError", databaseError.message)
        }
    }

    private fun handleData(users: List<User>) {
        val inactiveUsers: MutableList<User> = mutableListOf()

        for (user in users) {
            if (soldItems != null) {
                if (!soldItems!!.contains(user.email?.lowercase())) {
                    inactiveUsers.add(user)
                }
            }
        }

        setUI(inactiveUsers)
    }

    private fun setUI(inactiveUsers: List<User>) {
        binding.loading.visibility = View.GONE

        if (inactiveUsers.isEmpty()) {
            binding.rcInactiveUsers.visibility = View.GONE
            binding.inactiveUsersTitle.visibility = View.VISIBLE
        } else {
            binding.rcInactiveUsers.visibility = View.VISIBLE
            binding.inactiveUsersTitle.visibility = View.GONE

            adapter = InactiveUsersAdapter(inactiveUsers, this)
            val llm = LinearLayoutManager(requireContext())
            llm.orientation = LinearLayoutManager.VERTICAL
            binding.rcInactiveUsers.layoutManager = llm
            binding.rcInactiveUsers.adapter = adapter
        }
    }

    fun deleteUser(user: User) {
        FirebaseDatabase.getInstance().getReference("users/${user.id!!}").removeValue()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(postListener)
        usersDatabaseReference.removeEventListener(userPostListener)
        adapter = null
        _binding = null
    }

    companion object {
        val TAG = InactiveUsersFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): InactiveUsersFragment {
            val args = Bundle()
            val fragment = InactiveUsersFragment()
            fragment.arguments = args

            return fragment
        }
    }
}