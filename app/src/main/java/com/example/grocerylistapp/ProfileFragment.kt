package com.example.grocerylistapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.grocerylistapp.viewmodel.UserProfileViewModel
import com.example.grocerylistapp.viewmodel.UserProfileViewModelFactory
import com.example.grocerylistapp.repository.UserProfileRepository
import com.example.grocerylistapp.database.AppLocalDatabase
import com.example.grocerylistapp.database.UserProfile

class ProfileFragment : Fragment() {

    private lateinit var profileBanner: ImageView
    private lateinit var editProfileButton: ImageButton
    private lateinit var saveButton: View

    private lateinit var profileName: EditText
    private lateinit var profileLastName: EditText
    private lateinit var profileEmail: EditText

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var userViewModel: UserProfileViewModel

    private var selectedProfileImageResId = R.drawable.ic_profile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profileBanner = view.findViewById(R.id.profile_banner)
        editProfileButton = view.findViewById(R.id.edit_profile_button)
        saveButton = view.findViewById(R.id.save_profile_button)

        profileName = view.findViewById(R.id.profile_name)
        profileLastName = view.findViewById(R.id.profile_last_name)
        profileEmail = view.findViewById(R.id.profile_email)

        profileName.isEnabled = false
        profileLastName.isEnabled = false
        profileEmail.isEnabled = false
        profileBanner.isEnabled = false
        saveButton.visibility = View.GONE

        val dao = AppLocalDatabase.getInstance(requireContext()).userProfileDao()
        val repository = UserProfileRepository(dao)
        val factory = UserProfileViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, factory)[UserProfileViewModel::class.java]

        loadUserProfile()

        editProfileButton.setOnClickListener {
            profileName.isEnabled = true
            profileLastName.isEnabled = true
            profileBanner.isEnabled = true
            saveButton.visibility = View.VISIBLE

            profileBanner.setOnClickListener { showImagePickerDialog() }
        }

        saveButton.setOnClickListener {
            val name = profileName.text.toString().trim()
            val lastName = profileLastName.text.toString().trim()

            val currentUser = auth.currentUser
            if (currentUser != null) {
                val updates = mapOf(
                    "name" to name,
                    "lastName" to lastName,
                    "profileImageRes" to selectedProfileImageResId
                )
                firestore.collection("users").document(currentUser.uid)
                    .update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show()

                        val updatedUser = UserProfile(
                            email = currentUser.email ?: "",
                            name = name,
                            lastName = lastName,
                            profileImageRes = selectedProfileImageResId
                        )
                        userViewModel.insertOrUpdateUser(updatedUser)

                        profileName.isEnabled = false
                        profileLastName.isEnabled = false
                        profileBanner.isEnabled = false
                        saveButton.visibility = View.GONE
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to save profile: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email ?: return

            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: ""
                        val lastName = document.getString("lastName") ?: ""
                        val imageRes = document.getLong("profileImageRes")?.toInt() ?: R.drawable.ic_profile

                        profileName.setText(name)
                        profileLastName.setText(lastName)
                        profileEmail.setText(email)
                        profileBanner.setImageResource(imageRes)
                        selectedProfileImageResId = imageRes

                        val user = UserProfile(email, name, lastName, imageRes)
                        userViewModel.insertOrUpdateUser(user)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load from Firebase", Toast.LENGTH_SHORT).show()
                }

            userViewModel.getUser(email).observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    profileName.setText(user.name)
                    profileLastName.setText(user.lastName)
                    profileEmail.setText(user.email)
                    profileBanner.setImageResource(user.profileImageRes)
                    selectedProfileImageResId = user.profileImageRes
                }
            }
        }
    }

    private fun showImagePickerDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.profile_image_picker, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_profile_image))
            .setView(dialogView)
            .create()

        dialogView.findViewById<ImageView>(R.id.image_female_one).setOnClickListener {
            selectedProfileImageResId = R.drawable.female_profile_one
            profileBanner.setImageResource(selectedProfileImageResId)
            dialog.dismiss()
        }

        dialogView.findViewById<ImageView>(R.id.image_female_two).setOnClickListener {
            selectedProfileImageResId = R.drawable.female_profile_two
            profileBanner.setImageResource(selectedProfileImageResId)
            dialog.dismiss()
        }

        dialogView.findViewById<ImageView>(R.id.image_male_one).setOnClickListener {
            selectedProfileImageResId = R.drawable.male_profile_one
            profileBanner.setImageResource(selectedProfileImageResId)
            dialog.dismiss()
        }

        dialogView.findViewById<ImageView>(R.id.image_male_two).setOnClickListener {
            selectedProfileImageResId = R.drawable.male_profile_two
            profileBanner.setImageResource(selectedProfileImageResId)
            dialog.dismiss()
        }

        dialog.show()
    }
}
