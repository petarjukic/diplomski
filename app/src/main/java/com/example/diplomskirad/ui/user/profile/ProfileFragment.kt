package com.example.diplomskirad.ui.user.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.diplomskirad.MainActivity
import com.example.diplomskirad.R
import com.example.diplomskirad.common.Constants
import com.example.diplomskirad.common.preferences.LoginSharedPreferences
import com.example.diplomskirad.databinding.FragmentProfileBinding
import com.example.diplomskirad.model.SoldItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var signedIn: Boolean = false
    private var sharedPreferences: LoginSharedPreferences? = null
    private var totalProfit: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance().getReference("soldItems")

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        database.addValueEventListener(postListener)

        sharedPreferences = LoginSharedPreferences(requireContext())

        if (FirebaseAuth.getInstance().currentUser != null) {
            signedIn = true
        }

        setListener()
        return binding.root
    }


    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val item = child.getValue<SoldItems>()

                if (item != null) {
                    totalProfit += item.count!! * item.price!!
                }
            }
            setUI()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("databaseError", databaseError.message)
        }
    }

    private fun setUI() {
        if (signedIn) {
            binding.userEmail.visibility = View.VISIBLE
            binding.userEmailKey.visibility = View.VISIBLE
            binding.userEmail.text = auth.currentUser?.email
            binding.logoutUser.visibility = View.VISIBLE
            binding.ivLogout.visibility = View.VISIBLE
            binding.btnSignup.visibility = View.GONE
            binding.changePassword.visibility = View.VISIBLE
            binding.ivChangePassword.visibility = View.VISIBLE
        } else {
            binding.userEmail.visibility = View.GONE
            binding.userEmailKey.visibility = View.GONE
            binding.logoutUser.visibility = View.GONE
            binding.ivLogout.visibility = View.GONE
            binding.btnSignup.visibility = View.VISIBLE
            binding.changePassword.visibility = View.GONE
            binding.ivChangePassword.visibility = View.GONE
        }

        if (sharedPreferences?.getRole() == Constants().ADMIN_ROLE && signedIn) {
            binding.totalProfitKey.visibility = View.VISIBLE
            binding.totalProfitValue.text = StringBuilder("$").append(totalProfit.toString())
            binding.totalProfitValue.visibility = View.VISIBLE
        }
    }

    private fun setListener() {
        binding.logoutUser.setOnClickListener {
            logoutUser()
        }
        binding.ivLogout.setOnClickListener {
            logoutUser()
        }
        binding.btnSignup.setOnClickListener {
            findNavController().navigate(R.id.login_fragment)
        }
        binding.changePassword.setOnClickListener {
            findNavController().navigate(R.id.changePasswordFragment)
        }
        binding.ivChangePassword.setOnClickListener {
            findNavController().navigate(R.id.changePasswordFragment)
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        requireActivity().finish()
        requireActivity().startActivity(Intent(context, MainActivity::class.java))
        requireActivity().finishAffinity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.removeEventListener(postListener)
        _binding = null
    }

    companion object {
        val TAG = ProfileFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): ProfileFragment {
            val args = Bundle()
            val fragment = ProfileFragment()
            fragment.arguments = args

            return fragment
        }
    }
}