package com.example.diplomskirad.ui.user.change_password

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.diplomskirad.R
import com.example.diplomskirad.databinding.FragmentChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        binding.btnSend.setOnClickListener {
            changePassword()
        }

        return binding.root
    }

    private fun changePassword() {
        val isDifferentThanOldPassword = differentThanOldPassword()

        if (isDifferentThanOldPassword && repeatedPasswordCorrect()) {
            val newPassword = binding.registrationScreenPasswordTil.editText!!.text.toString()
            val oldPassword = binding.changePasswordOldTil.editText!!.text.toString()

            val user = FirebaseAuth.getInstance().currentUser
            val credential = EmailAuthProvider
                .getCredential(user?.email.toString(), oldPassword)

            user?.reauthenticate(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showSuccessfulMessage()
                        } else {
                            showErrorMessage(getString(R.string.change_password_fail))
                        }
                    }
                } else {
                    showErrorMessage(getString(R.string.change_password_fail))
                }
            }

        } else {
            if (isDifferentThanOldPassword) {
                showErrorMessage(getString(R.string.enter_valid_passwords))
            } else {
                showErrorMessage(getString(R.string.error_new_password_same_as_old_password))
            }
        }
    }

    private fun showSuccessfulMessage() {
        val message = getString(R.string.your_password_changed_successfully)

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    private fun showErrorMessage(message: String) {
        var message = message
        if (message.isEmpty()) {
            message = getString(R.string.invalid_old_password)
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    private fun repeatedPasswordCorrect(): Boolean {
        return if (binding.registrationScreenPasswordTil.editText!!.length() > 0 &&
            binding.registrationScreenPasswordRepeatTil.editText!!.length() > 0
        ) {
            binding.registrationScreenPasswordTil.editText!!
                .text.toString() == binding.registrationScreenPasswordRepeatTil.editText!!.text.toString()
        } else false
    }

    private fun differentThanOldPassword(): Boolean {
        return binding.registrationScreenPasswordTil.editText!!
            .text.toString() != binding.changePasswordOldTil.editText!!.text.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = ChangePasswordFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): ChangePasswordFragment {
            val args = Bundle()
            val fragment = ChangePasswordFragment()
            fragment.arguments = args

            return fragment
        }
    }
}