package com.example.grocerylistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class RegisterFragment : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var birthDateEditText: EditText
    private lateinit var signUpButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEditText = view.findViewById(R.id.regist_name)
        nameEditText = view.findViewById(R.id.regist_last_name)
        emailEditText = view.findViewById(R.id.Email_regist)
        passwordEditText = view.findViewById(R.id.Password_regist)
        birthDateEditText = view.findViewById(R.id.BirthDate_regist)
        signUpButton = view.findViewById(R.id.Sign_button)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val birthDate = birthDateEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || birthDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Save user or navigate
                Toast.makeText(requireContext(), "Registered successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
