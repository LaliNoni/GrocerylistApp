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
import com.example.grocerylistapp.model.UserItem
import com.example.grocerylistapp.viewmodel.ShoppingListViewModel
import com.example.grocerylistapp.model.GroceryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.grocerylistapp.util.AvailableGroceryItems


class EditListFragment : Fragment() {

    private lateinit var nameInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var saveButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemRecyclerView: RecyclerView

    private lateinit var adapter: UserItemAdapter
    private lateinit var imageAdapter: ImagePickerAdapter

    private val userItems = mutableListOf<UserItem>()

    private var selectedUserItemPosition: Int = -1
    private var listId: String? = null
    private var isDone: Boolean = false

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val shoppingListViewModel: ShoppingListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_edit_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameInput = view.findViewById(R.id.list_name_input)
        dateInput = view.findViewById(R.id.list_date_input)
        saveButton = view.findViewById(R.id.save_list_button)
        recyclerView = view.findViewById(R.id.userItemRecyclerView)
        itemRecyclerView = view.findViewById(R.id.itemRecyclerView)

        listId = arguments?.getString("listId")
        if (listId == null) {
            Toast.makeText(requireContext(), "List ID missing", Toast.LENGTH_SHORT).show()
            return
        }

        setupItemRecyclerView()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserItemAdapter(
            userItems,
            onAddItemBelow = { position ->
                val item = userItems[position]
                if (item.name.isNotBlank() && item.quantity > 0) {
                    userItems.add(position + 1, UserItem(imageRes = R.drawable.bake))
                    adapter.notifyItemInserted(position + 1)
                } else {
                    Toast.makeText(requireContext(), "Enter item name and quantity", Toast.LENGTH_SHORT).show()
                }
            },
            onImageClick = { position ->
                selectedUserItemPosition = position
                Toast.makeText(requireContext(), "Click an image to assign to item #${position + 1}", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter

        loadListData()

        saveButton.setOnClickListener {
            updateList()
        }
    }

    private fun setupItemRecyclerView() {
        imageAdapter = ImagePickerAdapter(AvailableGroceryItems.list) { selectedItem, isSelected ->
            if (isSelected && selectedUserItemPosition in userItems.indices) {
                userItems[selectedUserItemPosition].imageRes = selectedItem.imageRes
                adapter.notifyItemChanged(selectedUserItemPosition)
                selectedUserItemPosition = -1
            }
        }
        itemRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        itemRecyclerView.adapter = imageAdapter
    }

    private fun loadListData() {
        val currentUser = auth.currentUser ?: return

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("groceryLists")
            .document(listId!!)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    nameInput.setText(doc.getString("name") ?: "")
                    dateInput.setText(doc.getString("date") ?: "")
                    isDone = doc.getBoolean("isDone") ?: false

                    val itemsList = doc.get("items") as? List<*>
                    userItems.clear()
                    itemsList?.mapNotNull { item ->
                        val map = item as? Map<*, *> ?: return@mapNotNull null
                        val imageName = map["imageName"] as? String ?: "bake"
                        val isChecked = map["isChecked"] as? Boolean ?: false

                        UserItem(
                            name = map["name"] as? String ?: "",
                            quantity = (map["quantity"] as? Number)?.toInt() ?: 0,
                            isChecked = isChecked,
                            imageRes = getImageResIdByName(imageName)
                        )
                    }?.let { userItems.addAll(it) }

                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load list", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateList() {
        val currentUser = auth.currentUser ?: return
        val name = nameInput.text.toString().trim()
        val date = dateInput.text.toString().trim()

        if (name.isEmpty() || date.isEmpty()) {
            Toast.makeText(requireContext(), "Fill name and date", Toast.LENGTH_SHORT).show()
            return
        }

        val validItems = userItems.filter { it.name.isNotBlank() && it.quantity > 0 }
        if (validItems.isEmpty()) {
            Toast.makeText(requireContext(), "Add at least one valid item", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedList = hashMapOf(
            "name" to name,
            "date" to date,
            "isDone" to isDone,
            "items" to validItems.map { item ->
                hashMapOf(
                    "name" to item.name,
                    "quantity" to item.quantity,
                    "isChecked" to item.isChecked,
                    "imageName" to (AvailableGroceryItems.list.find { g -> g.imageRes == item.imageRes }?.imageName ?: "bake")
                )
            }
        )

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("groceryLists")
            .document(listId!!)
            .set(updatedList)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "List updated", Toast.LENGTH_SHORT).show()

                val roomList = ShoppingListRoom(
                    id = listId!!,
                    name = name,
                    date = date,
                    imageResId = R.drawable.bake,
                    lastUpdated = System.currentTimeMillis(),
                    isDone = isDone
                )
                shoppingListViewModel.insertLists(roomList)
                findNavController().popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update list", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getImageResIdByName(name: String): Int {
        val resId = resources.getIdentifier(name, "drawable", requireContext().packageName)
        return if (resId != 0) resId else R.drawable.bake
    }
}
