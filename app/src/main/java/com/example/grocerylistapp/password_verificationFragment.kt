package com.example.grocerylistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class PasswordVerificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_password_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newPassword = view.findViewById<EditText>(R.id.new_password)
        val confirmPassword = view.findViewById<EditText>(R.id.confirm_password)
        val saveButton = view.findViewById<Button>(R.id.save_password_button)

        saveButton.setOnClickListener {
            val newPass = newPassword.text.toString()
            val confirmPass = confirmPassword.text.toString()

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill both fields", Toast.LENGTH_SHORT).show()
            } else if (newPass != confirmPass) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show()

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LogInFragment())
                    .commit()
            }
        }
    }
}
