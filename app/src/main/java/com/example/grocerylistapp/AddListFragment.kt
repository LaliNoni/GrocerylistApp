package com.example.grocerylistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.adapter.ImagePickerAdapter
import com.example.grocerylistapp.adapter.UserItemAdapter
import com.example.grocerylistapp.model.GroceryItem
import com.example.grocerylistapp.model.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddListFragment : Fragment() {

    private lateinit var nameInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var addButton: Button

    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var userItemRecyclerView: RecyclerView

    private lateinit var userItemAdapter: UserItemAdapter
    private val userItem = mutableListOf<UserItem>()
    private val selectedItems = mutableListOf<GroceryItem>()

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
        super.onViewCreated(view, savedInstanceState)

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
                val isNameValid = currentItem.name.isNotBlank()
                val isQuantityValid = currentItem.quantity > 0

                if (isNameValid && isQuantityValid) {
                    userItem.add(position + 1, UserItem(imageRes = R.drawable.bake))
                    userItemAdapter.notifyItemInserted(position + 1)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please enter item name and quantity before adding a new item",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onImageClick = { position ->
                selectedUserItemPosition = position
                Toast.makeText(requireContext(), "Select an image below to replace item #${position + 1} image", Toast.LENGTH_SHORT).show()
            }
        )

        userItemRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userItemRecyclerView.adapter = userItemAdapter

        setupItemRecyclerView()

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val date = dateInput.text.toString().trim()

            if (name.isEmpty() || date.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both name and date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val validUserItems = userItem.filter { it.name.isNotBlank() && it.quantity > 0 }

            if (validUserItems.isEmpty()) {
                Toast.makeText(requireContext(), "Add at least one valid grocery item", Toast.LENGTH_SHORT).show()
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
                        "imageName" to it.imageRes
                    )
                }
            )

            firestore.collection("users")
                .document(currentUser.uid)
                .collection("groceryLists")
                .add(listData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "List \"$name\" saved!", Toast.LENGTH_SHORT).show()
                    // Reset inputs after save
                    nameInput.text.clear()
                    dateInput.text.clear()
                    selectedItems.clear()
                    userItem.clear()
                    userItem.add(UserItem(imageRes = R.drawable.bake))
                    userItemAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to save list: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun setupItemRecyclerView() {
        val availableItems = listOf(
            //Fruits
            GroceryItem("Apple", "?", R.drawable.apple),
            GroceryItem("Apricot", "?", R.drawable.apricot),
            GroceryItem("Banana", "?", R.drawable.banana),
            GroceryItem("Dragon fruit", "?", R.drawable.dragonfruit),
            GroceryItem("Lemon", "?", R.drawable.lemon),
            GroceryItem("Mango", "?", R.drawable.mango),
            GroceryItem("Papaya", "?", R.drawable.papaya),
            GroceryItem("Peach", "?", R.drawable.peach),
            GroceryItem("Plum", "?", R.drawable.plum),
            GroceryItem("Zucchini", "?", R.drawable.zucchini),

            //Vegetables
            GroceryItem("Tomato", "?", R.drawable.tomato),
            GroceryItem("Lettuce", "?", R.drawable.lettuce),
            GroceryItem("Cucumber", "?", R.drawable.cucumber),
            GroceryItem("Chili", "?", R.drawable.chili),
            GroceryItem("Corn", "?", R.drawable.corn),
            GroceryItem("Edamame", "?", R.drawable.edamame),
            GroceryItem("Eggplant", "?", R.drawable.eggplant),
            GroceryItem("Green beans", "?", R.drawable.green_beans),
            GroceryItem("Green pepper", "?", R.drawable.greenpepper),
            GroceryItem("Onion Purple", "?", R.drawable.purple_onion),
            GroceryItem("Onion White", "?", R.drawable.white_onion),
            GroceryItem("Pumpkin", "?", R.drawable.pumpkin),
            GroceryItem("Red Cabbage", "?", R.drawable.red_cabbage),
            GroceryItem("Yellow Pepper", "?", R.drawable.yellowpepper),

            //Dairy
            GroceryItem("Cheese", "?", R.drawable.cheese),
            GroceryItem("Milk", "?", R.drawable.milk),
            GroceryItem("Yogurt", "?", R.drawable.yogurt),
            GroceryItem("Butter", "?", R.drawable.butter),
            GroceryItem("Eggs", "?", R.drawable.egg),

            //Canned Food
            GroceryItem("Canned food", "?", R.drawable.canned_food),
            GroceryItem("Canned Fish", "?", R.drawable.canned_fish),
            GroceryItem("Tinned Food", "?", R.drawable.tinned_food),

            //Bakery
            GroceryItem("White Bread", "?", R.drawable.white_bread),
            GroceryItem("Whole Wheat Bread", "?", R.drawable.whole_wheat_bread),
            GroceryItem("Baguette", "?", R.drawable.baguette),
            GroceryItem("Pizza", "?", R.drawable.pizza),
            GroceryItem("Bake", "?", R.drawable.bake),

            //Condiments
            GroceryItem("Salt", "?", R.drawable.salt),
            GroceryItem("Baking Soda", "?", R.drawable.baking_soda),
            GroceryItem("Olive Oil", "?", R.drawable.olive_oil),
            GroceryItem("Marinara Sauce", "?", R.drawable.marinara_sauce),

            //Proteins
            GroceryItem("Fish", "?", R.drawable.fish),
            GroceryItem("Salmon", "?", R.drawable.salmon),
            GroceryItem("Salami", "?", R.drawable.salami),

            //Snacks
            GroceryItem("Snack", "?", R.drawable.snack),
            GroceryItem("Chocolate", "?", R.drawable.chocolate),
            GroceryItem("Popcorn", "?", R.drawable.popcorn),
            GroceryItem("Pickles", "?", R.drawable.pickles),
            GroceryItem("Olive", "?", R.drawable.olive),

            //Beverages
            GroceryItem("Water Bottle", "?", R.drawable.water_bottle),
            GroceryItem("Tea", "?", R.drawable.tea),
            GroceryItem("Coffee Beans", "?", R.drawable.coffee_beans),
            GroceryItem("Apple Juice", "?", R.drawable.apple_juice),
            GroceryItem("Cider", "?", R.drawable.cider),
            GroceryItem("Juice", "?", R.drawable.juice),
            GroceryItem("Red Wine", "?", R.drawable.red_wine),
            GroceryItem("Gallon", "?", R.drawable.gallon)
        )

        val adapter = ImagePickerAdapter(availableItems) { selectedItem, isSelected ->
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
