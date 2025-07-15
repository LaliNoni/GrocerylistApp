package com.example.grocerylistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.navigation.fragment.findNavController
import com.example.grocerylistapp.adapter.GroceryListAdapter
import com.example.grocerylistapp.database.ShoppingListRoom
import com.example.grocerylistapp.viewmodel.ShoppingListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GroceryListsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: GroceryListAdapter
    private val groceryLists = mutableListOf<ShoppingListRoom>()

    private val viewModel: ShoppingListViewModel by viewModels()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_grocery_lists, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.groceryRecyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        adapter = GroceryListAdapter(
            groceryLists,
            onCheckedChange = { position, isChecked ->
                val list = groceryLists[position]
                list.isDone = isChecked
                viewModel.updateShoppingList(list)
            },
            onDeleteClick = { position ->
                val list = groceryLists[position]
                deleteListFromFirestore(list.id) { success ->
                    if (success) {
                        viewModel.deleteList(list)
                        Toast.makeText(requireContext(), "List deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete from Firebase", Toast.LENGTH_SHORT).show()
                    }
                }
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

        viewModel.allShoppingLists.observe(viewLifecycleOwner) { lists ->
            groceryLists.clear()
            groceryLists.addAll(lists)
            adapter.notifyDataSetChanged()
        }

        swipeRefreshLayout.setOnRefreshListener {
            syncFromFirebase()
        }

        swipeRefreshLayout.isRefreshing = true
        syncFromFirebase()
    }

    private fun syncFromFirebase() {
        viewModel.syncListsFromFirestore { success, message ->
            swipeRefreshLayout.isRefreshing = false
            if (!success) {
                Toast.makeText(requireContext(), "Sync failed: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteListFromFirestore(listId: String, callback: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false)
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("groceryLists")
            .document(listId)
            .delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
}
