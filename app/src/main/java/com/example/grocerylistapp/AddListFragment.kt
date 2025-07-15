package com.example.grocerylistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.adapter.ImagePickerAdapter
import com.example.grocerylistapp.adapter.UserItemAdapter
import com.example.grocerylistapp.database.ShoppingListRoom
import com.example.grocerylistapp.model.GroceryItem
import com.example.grocerylistapp.model.UserItem
import com.example.grocerylistapp.viewmodel.ShoppingListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.grocerylistapp.util.AvailableGroceryItems

class AddListFragment : Fragment() {

    private lateinit var nameInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var addButton: Button

    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var userItemRecyclerView: RecyclerView

    private lateinit var userItemAdapter: UserItemAdapter
    private val userItem = mutableListOf<UserItem>()
    private val selectedItems = mutableListOf<GroceryItem>()

    private val shoppingListViewModel: ShoppingListViewModel by viewModels()

    private var selectedUserItemPosition: Int = -1

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nameInput = view.findViewById(R.id.list_name_input)
        dateInput = view.findViewById(R.id.list_date_input)
        addButton = view.findViewById(R.id.add_list_button)
        itemRecyclerView = view.findViewById(R.id.itemRecyclerView)
        userItemRecyclerView = view.findViewById(R.id.userItemRecyclerView)

        userItem.add(UserItem(imageRes = R.drawable.bake))

        userItemAdapter = UserItemAdapter(
            userItem,
            onAddItemBelow = { position ->
                val currentItem = userItem[position]
                if (currentItem.name.isNotBlank() && currentItem.quantity > 0) {
                    userItem.add(position + 1, UserItem(imageRes = R.drawable.bake))
                    userItemAdapter.notifyItemInserted(position + 1)
                } else {
                    Toast.makeText(requireContext(), "Enter item name and quantity first", Toast.LENGTH_SHORT).show()
                }
            },
            onImageClick = { position ->
                selectedUserItemPosition = position
                Toast.makeText(requireContext(), "Select an image for item #${position + 1}", Toast.LENGTH_SHORT).show()
            }
        )

        userItemRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userItemRecyclerView.adapter = userItemAdapter

        setupItemRecyclerView()

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val date = dateInput.text.toString().trim()

            if (name.isEmpty() || date.isEmpty()) {
                Toast.makeText(requireContext(), "Enter both name and date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val validUserItems = userItem.filter { it.name.isNotBlank() && it.quantity > 0 }

            if (validUserItems.isEmpty()) {
                Toast.makeText(requireContext(), "Add at least one valid item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val listData = hashMapOf(
                "name" to name,
                "date" to date,
                "isDone" to false,
                "items" to validUserItems.map {
                    hashMapOf(
                        "name" to it.name,
                        "quantity" to it.quantity,
                        "imageName" to (AvailableGroceryItems.list.find { item -> item.imageRes == it.imageRes }?.imageName ?: "bake")
                    )
                }
            )

            firestore.collection("users")
                .document(currentUser.uid)
                .collection("groceryLists")
                .add(listData)
                .addOnSuccessListener { documentRef ->
                    Toast.makeText(requireContext(), "List \"$name\" saved!", Toast.LENGTH_SHORT).show()

                    val listRoom = ShoppingListRoom(
                        id = documentRef.id,
                        name = name,
                        date = date,
                        imageResId = R.drawable.bake,
                        lastUpdated = System.currentTimeMillis()
                    )
                    shoppingListViewModel.insertLists(listRoom)

                    findNavController().navigate(R.id.groceryListsFragment)

                    nameInput.text.clear()
                    dateInput.text.clear()
                    selectedItems.clear()
                    userItem.clear()
                    userItem.add(UserItem(imageRes = R.drawable.bake))
                    userItemAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun setupItemRecyclerView() {
        val adapter = ImagePickerAdapter(AvailableGroceryItems.list) { selectedItem, isSelected ->
            if (isSelected) {
                selectedItems.add(selectedItem)
                Toast.makeText(requireContext(), "${selectedItem.name} added", Toast.LENGTH_SHORT).show()

                if (selectedUserItemPosition in userItem.indices) {
                    userItem[selectedUserItemPosition].imageRes = selectedItem.imageRes
                    userItemAdapter.notifyItemChanged(selectedUserItemPosition)
                    selectedUserItemPosition = -1
                }
            } else {
                selectedItems.remove(selectedItem)
                Toast.makeText(requireContext(), "${selectedItem.name} removed", Toast.LENGTH_SHORT).show()
            }
        }

        itemRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
        itemRecyclerView.adapter = adapter
    }
}
