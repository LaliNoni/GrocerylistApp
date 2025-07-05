package com.example.grocerylistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class PasswordResetFragment : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var cancelButton: Button
    private lateinit var resetButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_password_reset, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailEditText = view.findViewById(R.id.Reset_email)
        passwordEditText = view.findViewById(R.id.Reset_passward)
        cancelButton = view.findViewById(R.id.Cancel_reset_button)
        resetButton = view.findViewById(R.id.Reset_reset_button)

        cancelButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LogInFragment())
                .addToBackStack(null)
                .commit()
        }

        resetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val newPassword = passwordEditText.text.toString().trim()

            if (email.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter email and new password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Add your password reset logic here
            // Example: call your Firebase or backend API to reset password

            Toast.makeText(requireContext(), "Password reset successful!", Toast.LENGTH_SHORT).show()

            // Navigate back to login screen
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LogInFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
