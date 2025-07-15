package com.example.grocerylistapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var nameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var birthDateEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nameEditText = view.findViewById(R.id.regist_name)
        lastNameEditText = view.findViewById(R.id.regist_last_name)
        emailEditText = view.findViewById(R.id.Email_regist)
        passwordEditText = view.findViewById(R.id.Password_regist)
        birthDateEditText = view.findViewById(R.id.BirthDate_regist)
        signUpButton = view.findViewById(R.id.Sign_button)
        progressBar = view.findViewById(R.id.progressBar)

        birthDateEditText.isFocusable = false
        birthDateEditText.isClickable = true

        birthDateEditText.setOnClickListener {
            val defaultYear = 1998
            val defaultMonth = 0
            val defaultDay = 1

            val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                birthDateEditText.setText(formattedDate)
            }, defaultYear, defaultMonth, defaultDay)

            datePicker.datePicker.maxDate = System.currentTimeMillis()

            datePicker.show()
        }

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val birthDate = birthDateEditText.text.toString().trim()

            if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || birthDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userInfo = hashMapOf(
                            "name" to name,
                            "lastName" to lastName,
                            "birthDate" to birthDate,
                            "email" to email
                        )

                        user?.let {
                            db.collection("users").document(it.uid)
                                .set(userInfo)
                                .addOnSuccessListener {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(requireContext(), "Registered successfully!", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_registerFragment_to_groceryListsFragment)
                                }
                                .addOnFailureListener { e ->
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(requireContext(), "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        val errorMessage = task.exception?.message ?: "Registration failed."
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
