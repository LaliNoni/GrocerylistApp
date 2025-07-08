package com.example.grocerylistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class LogInFragment : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailEditText = view.findViewById(R.id.Email_log)
        passwordEditText = view.findViewById(R.id.Password_log)
        loginButton = view.findViewById(R.id.LogIn_button)
        forgotPasswordTextView = view.findViewById(R.id.text_forgot_password)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                // Navigate to grocery list after login
                findNavController().navigate(R.id.action_logInFragment_to_groceryListsFragment)
            }
        }

        forgotPasswordTextView.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_passwordResetFragment)
        }
    }
}
