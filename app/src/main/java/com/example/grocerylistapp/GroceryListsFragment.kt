package com.example.grocerylistapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.grocerylistapp.adapter.GroceryListAdapter
import com.example.grocerylistapp.model.GroceryListModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class GroceryListsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroceryListAdapter
    private val groceryLists = mutableListOf<GroceryListModel>()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var firestoreListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_grocery_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.groceryRecyclerView)

        adapter = GroceryListAdapter(
            groceryLists,
            onCheckedChange = { position, isChecked ->
                val list = groceryLists[position]
                list.isDone = isChecked
                updateIsDoneInFirestore(list)
            },
            onDeleteClick = { position ->
                val list = groceryLists[position]
                deleteListFromFirestore(list.id)
            },

            onListClick = { list ->
                val bundle = Bundle().apply {
                    putString("listId", list.id)
                }
                findNavController().navigate(R.id.action_groceryListsFragment_to_editListFragment, bundle)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        loadGroceryLists()
    }

    private fun loadGroceryLists() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        firestoreListener = firestore.collection("users")
            .document(currentUser.uid)
            .collection("groceryLists")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Failed to load lists: ${error.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    groceryLists.clear()
                    for (doc in snapshot.documents) {
                        val id = doc.id
                        val name = doc.getString("name") ?: "No Name"
                        val date = doc.getString("date") ?: ""
                        val isDone = doc.getBoolean("isDone") ?: false

                        groceryLists.add(
                            GroceryListModel(
                                id = id,
                                name = name,
                                date = date,
                                isDone = isDone
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    private fun updateIsDoneInFirestore(list: GroceryListModel) {
        val currentUser = auth.currentUser ?: return
        firestore.collection("users")
            .document(currentUser.uid)
            .collection("groceryLists")
            .document(list.id)
            .update("isDone", list.isDone)
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update status", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteListFromFirestore(listId: String) {
        val currentUser = auth.currentUser ?: return
        firestore.collection("users")
            .document(currentUser.uid)
            .collection("groceryLists")
            .document(listId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "List deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete list", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firestoreListener?.remove()
    }
}
