package com.example.grocerylistapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

data class UserModel(
    val uid: String = "",
    val name: String = ""
)

class SearchFragment : Fragment() {

    private lateinit var searchInput: EditText
    private lateinit var searchButton: Button

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchInput = view.findViewById(R.id.search_input)
        searchButton = view.findViewById(R.id.search_button)

        searchButton.setOnClickListener {
            val queryText = searchInput.text.toString().trim()
            if (queryText.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a name to search", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            searchUsersByName(queryText)
        }
    }

    private fun searchUsersByName(nameQuery: String) {
        val lowerCaseQuery = nameQuery.toLowerCase()
        val upperBound = nameQuery.toLowerCase() + '\uf8ff'

        firestore.collection("users")
            .whereGreaterThanOrEqualTo("name", lowerCaseQuery)
            .whereLessThanOrEqualTo("name", upperBound)
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.mapNotNull { doc ->
                    val uid = doc.id
                    val name = doc.getString("name") ?: ""
                    UserModel(uid, name)
                }.filter { it.name.contains(nameQuery, ignoreCase = true) }

                if (users.isEmpty()) {
                    Toast.makeText(requireContext(), "No users found", Toast.LENGTH_SHORT).show()
                } else {
                    showUsersDialog(users)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Search failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showUsersDialog(users: List<UserModel>) {
        val userNames = users.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Select a user")
            .setItems(userNames) { _, which ->
                val selectedUser = users[which]
                Toast.makeText(requireContext(), "Selected: ${selectedUser.name}", Toast.LENGTH_SHORT).show()
                val bundle = Bundle().apply {
                    putString("userId", selectedUser.uid)
                    putString("userName", selectedUser.name)
                }
                findNavController().navigate(R.id.action_searchFragment_to_userListFragment, bundle)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
