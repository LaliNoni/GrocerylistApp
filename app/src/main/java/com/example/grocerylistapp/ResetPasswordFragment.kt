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
    private lateinit var birthdateEditText: EditText
    private lateinit var cancelButton: Button
    private lateinit var verifyButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_password_reset, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailEditText = view.findViewById(R.id.email_reset)
        birthdateEditText = view.findViewById(R.id.birthdate_reset)
        cancelButton = view.findViewById(R.id.Cancel_reset_button)
        verifyButton = view.findViewById(R.id.verify_button)

        cancelButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LogInFragment())
                .addToBackStack(null)
                .commit()
        }

        verifyButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val birthdate = birthdateEditText.text.toString().trim()

            if (email.isEmpty() || birthdate.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both email and birthdate", Toast.LENGTH_SHORT).show()
            } else {
                // Optionally add email and birthdate verification logic here

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PasswordVerificationFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}
