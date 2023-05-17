package com.example.diplomskirad.ui.user.user_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomskirad.R
import com.example.diplomskirad.common.preferences.LoginSharedPreferences
import com.example.diplomskirad.databinding.FragmentUsersBinding
import com.example.diplomskirad.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersFragment : Fragment() {
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private var adapter: UsersAdapter? = null
    private lateinit var database: DatabaseReference
    private var userList: MutableList<User>? = null
    private var sharedPreferences: LoginSharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("users")
        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        userList = ArrayList()
        sharedPreferences = LoginSharedPreferences(requireContext())

        Log.d("provjera", "AAA ov je user ${sharedPreferences?.getEmail()}")

        adapter = UsersAdapter(userList, this)
        val llm = LinearLayoutManager(requireContext())
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rcAllUsers.layoutManager = llm
        binding.rcAllUsers.adapter = adapter
        database.addValueEventListener(postListener)

        return binding.root
    }

    fun navigateToDetails(bundle: Bundle) {
        findNavController().navigate(R.id.user_details_fragment, bundle)
    }

    fun removeUser(userId: String?) {
        database.child("${userId}/isRemoved").setValue(true)
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val value: User? = child.getValue(User::class.java)

                if (value != null && !value.isRemoved) {
                    userList?.add(value)
                }
            }

            adapter?.notifyDataSetChanged()
            binding.loading.visibility = View.GONE
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Toast.makeText(context, "Unable to load data.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userList = ArrayList()
        database.removeEventListener(postListener)
        adapter = null
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