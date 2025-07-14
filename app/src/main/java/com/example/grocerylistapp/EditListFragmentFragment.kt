package com.example.grocerylistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.adapter.UserItemAdapter
import com.example.grocerylistapp.adapter.ImagePickerAdapter
import com.example.grocerylistapp.model.GroceryItem
import com.example.grocerylistapp.model.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val availableItems = listOf(
        GroceryItem("Apple", "apple", R.drawable.apple),
        GroceryItem("Apricot", "apricot", R.drawable.apricot),
        GroceryItem("Banana", "banana", R.drawable.banana),
        GroceryItem("Dragon fruit", "dragonfruit", R.drawable.dragonfruit),
        GroceryItem("Lemon", "lemon", R.drawable.lemon),
        GroceryItem("Mango", "mango", R.drawable.mango),
        GroceryItem("Papaya", "papaya", R.drawable.papaya),
        GroceryItem("Peach", "peach", R.drawable.peach),
        GroceryItem("Plum", "plum", R.drawable.plum),
        GroceryItem("Zucchini", "zucchini", R.drawable.zucchini),

        GroceryItem("Tomato", "tomato", R.drawable.tomato),
        GroceryItem("Lettuce", "lettuce", R.drawable.lettuce),
        GroceryItem("Cucumber", "cucumber", R.drawable.cucumber),
        GroceryItem("Chili", "chili", R.drawable.chili),
        GroceryItem("Corn", "corn", R.drawable.corn),
        GroceryItem("Edamame", "edamame", R.drawable.edamame),
        GroceryItem("Eggplant", "eggplant", R.drawable.eggplant),
        GroceryItem("Green beans", "green_beans", R.drawable.green_beans),
        GroceryItem("Green pepper", "greenpepper", R.drawable.greenpepper),
        GroceryItem("Onion Purple", "purple_onion", R.drawable.purple_onion),
        GroceryItem("Onion White", "white_onion", R.drawable.white_onion),
        GroceryItem("Pumpkin", "pumpkin", R.drawable.pumpkin),
        GroceryItem("Red Cabbage", "red_cabbage", R.drawable.red_cabbage),
        GroceryItem("Yellow Pepper", "yellowpepper", R.drawable.yellowpepper),

        GroceryItem("Cheese", "cheese", R.drawable.cheese),
        GroceryItem("Milk", "milk", R.drawable.milk),
        GroceryItem("Yogurt", "yogurt", R.drawable.yogurt),
        GroceryItem("Butter", "butter", R.drawable.butter),
        GroceryItem("Eggs", "egg", R.drawable.egg),

        GroceryItem("Canned food", "canned_food", R.drawable.canned_food),
        GroceryItem("Canned Fish", "canned_fish", R.drawable.canned_fish),
        GroceryItem("Tinned Food", "tinned_food", R.drawable.tinned_food),

        GroceryItem("White Bread", "white_bread", R.drawable.white_bread),
        GroceryItem("Whole Wheat Bread", "whole_wheat_bread", R.drawable.whole_wheat_bread),
        GroceryItem("Baguette", "baguette", R.drawable.baguette),
        GroceryItem("Pizza", "pizza", R.drawable.pizza),
        GroceryItem("Bake", "bake", R.drawable.bake),

        GroceryItem("Salt", "salt", R.drawable.salt),
        GroceryItem("Baking Soda", "baking_soda", R.drawable.baking_soda),
        GroceryItem("Olive Oil", "olive_oil", R.drawable.olive_oil),
        GroceryItem("Marinara Sauce", "marinara_sauce", R.drawable.marinara_sauce),

        GroceryItem("Fish", "fish", R.drawable.fish),
        GroceryItem("Salmon", "salmon", R.drawable.salmon),
        GroceryItem("Salami", "salami", R.drawable.salami),

        GroceryItem("Snack", "snack", R.drawable.snack),
        GroceryItem("Chocolate", "chocolate", R.drawable.chocolate),
        GroceryItem("Popcorn", "popcorn", R.drawable.popcorn),
        GroceryItem("Pickles", "pickles", R.drawable.pickles),
        GroceryItem("Olive", "olive", R.drawable.olive),

        GroceryItem("Water Bottle", "water_bottle", R.drawable.water_bottle),
        GroceryItem("Tea", "tea", R.drawable.tea),
        GroceryItem("Coffee Beans", "coffee_beans", R.drawable.coffee_beans),
        GroceryItem("Apple Juice", "apple_juice", R.drawable.apple_juice),
        GroceryItem("Cider", "cider", R.drawable.cider),
        GroceryItem("Juice", "juice", R.drawable.juice),
        GroceryItem("Red Wine", "red_wine", R.drawable.red_wine),
        GroceryItem("Gallon", "gallon", R.drawable.gallon)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_edit_list, container, false)
    }

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

        setupItemRecyclerView()
        loadListData()

        saveButton.setOnClickListener {
            updateList()
        }
    }

    private fun setupItemRecyclerView() {
        imageAdapter = ImagePickerAdapter(availableItems) { selectedItem, isSelected ->
            if (isSelected && selectedUserItemPosition in userItems.indices) {
                userItems[selectedUserItemPosition].imageRes = selectedItem.imageRes
                adapter.notifyItemChanged(selectedUserItemPosition)
                selectedUserItemPosition = -1
            }
        }
        itemRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
        itemRecyclerView.adapter = imageAdapter
    }

    private fun loadListData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("groceryLists")
            .document(listId!!)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    nameInput.setText(doc.getString("name") ?: "")
                    dateInput.setText(doc.getString("date") ?: "")

                    val itemsList = doc.get("items") as? List<*>
                    userItems.clear()
                    if (itemsList != null) {
                        userItems.addAll(itemsList.mapNotNull { item ->
                            val map = item as? Map<*, *> ?: return@mapNotNull null
                            val imageName = map["imageName"] as? String ?: "bake"
                            UserItem(
                                name = map["name"] as? String ?: "",
                                quantity = (map["quantity"] as? Number)?.toInt() ?: 0,
                                isChecked = false,
                                imageRes = getImageResIdByName(imageName)
                            )
                        })
                    }
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
            "isDone" to false,
            "items" to validItems.map { item ->
                hashMapOf(
                    "name" to item.name,
                    "quantity" to item.quantity,
                    "imageName" to (availableItems.find { g -> g.imageRes == item.imageRes }?.imageName ?: "bake")                )
            }
        )

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("groceryLists")
            .document(listId!!)
            .set(updatedList)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "List updated", Toast.LENGTH_SHORT).show()

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
