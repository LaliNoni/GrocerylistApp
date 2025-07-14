package com.example.grocerylistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.adapter.UserItemAdapter
import com.example.grocerylistapp.model.GroceryListModel
import com.example.grocerylistapp.model.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ItemUserInputFragment : Fragment() {

    private lateinit var listNameInput: EditText
    private lateinit var listDateInput: EditText
    private lateinit var addListButton: Button

    private lateinit var userItemRecyclerView: RecyclerView
    private lateinit var addItemButton: Button

    private val userItems = mutableListOf<UserItem>()
    private lateinit var adapter: UserItemAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_item_user_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listNameInput = view.findViewById(R.id.list_name_input)
        listDateInput = view.findViewById(R.id.list_date_input)
        addListButton = view.findViewById(R.id.add_list_button)
        userItemRecyclerView = view.findViewById(R.id.userItemRecyclerView)
        addItemButton = view.findViewById(R.id.add_item_button)

        userItems.add(UserItem())

        adapter = UserItemAdapter(
            userItems,
            onAddItemBelow = { position ->
                val item = userItems[position]
                if (item.name.isNotBlank() && item.quantity > 0) {
                    userItems.add(position + 1, UserItem())
                    adapter.notifyItemInserted(position + 1)
                } else {
                    Toast.makeText(requireContext(), "Fill item name and quantity first", Toast.LENGTH_SHORT).show()
                }
            },
            onImageClick = { position ->
                Toast.makeText(requireContext(), "Image clicked at position $position", Toast.LENGTH_SHORT).show()

            }
        )

        userItemRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userItemRecyclerView.adapter = adapter

        addItemButton.setOnClickListener {
            userItems.add(UserItem())
            adapter.notifyItemInserted(userItems.size - 1)
        }

        addListButton.setOnClickListener {
            saveGroceryList()
        }
    }

    private fun saveGroceryList() {
        val name = listNameInput.text.toString().trim()
        val date = listDateInput.text.toString().trim()

        if (name.isEmpty() || date.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter list name and date", Toast.LENGTH_SHORT).show()
            return
        }

        val validItems = userItems.filter { it.name.isNotBlank() && it.quantity > 0 }
        if (validItems.isEmpty()) {
            Toast.makeText(requireContext(), "Add at least one valid grocery item", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val groceryList = GroceryListModel(
            name = name,
            date = date,
            userId = currentUser.uid,
            items = validItems,
            isDone = false
        )

        // Save under the user's collection "groceryLists"
        firestore.collection("users")
            .document(currentUser.uid)
            .collection("groceryLists")
            .add(groceryList)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "List saved successfully!", Toast.LENGTH_SHORT).show()

                // Clear inputs and list
                listNameInput.text.clear()
                listDateInput.text.clear()
                userItems.clear()
                userItems.add(UserItem())
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to save list: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
