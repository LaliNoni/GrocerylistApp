package com.example.grocerylistapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var profileBanner: ImageView
    private lateinit var editProfileButton: ImageButton
    private lateinit var saveButton: View

    private lateinit var profileName: EditText
    private lateinit var profileLastName: EditText
    private lateinit var profileEmail: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profileBanner = view.findViewById(R.id.profile_banner)
        editProfileButton = view.findViewById(R.id.edit_profile_button)
        saveButton = view.findViewById(R.id.save_profile_button)

        profileName = view.findViewById(R.id.profile_name)
        profileLastName = view.findViewById(R.id.profile_last_name)
        profileEmail = view.findViewById(R.id.profile_email)

        profileEmail.isEnabled = false

        val openImagePicker = View.OnClickListener { showImagePickerDialog() }
        profileBanner.setOnClickListener(openImagePicker)
        editProfileButton.setOnClickListener(openImagePicker)

        saveButton.setOnClickListener {
            val name = profileName.text.toString().trim()
            val lastName = profileLastName.text.toString().trim()

            if (name.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(requireContext(), "Name and Last Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Save the profile data (e.g. to database or shared prefs)

            Toast.makeText(requireContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show()
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
            profileBanner.setImageResource(R.drawable.female_profile_one)
            dialog.dismiss()
        }

        dialogView.findViewById<ImageView>(R.id.image_female_two).setOnClickListener {
            profileBanner.setImageResource(R.drawable.female_profile_two)
            dialog.dismiss()
        }

        dialogView.findViewById<ImageView>(R.id.image_male_one).setOnClickListener {
            profileBanner.setImageResource(R.drawable.male_profile_one)
            dialog.dismiss()
        }

        dialogView.findViewById<ImageView>(R.id.image_male_two).setOnClickListener {
            profileBanner.setImageResource(R.drawable.male_profile_two)
            dialog.dismiss()
        }

        dialog.show()
    }
}
