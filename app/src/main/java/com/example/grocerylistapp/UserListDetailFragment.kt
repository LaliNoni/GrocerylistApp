package com.example.grocerylistapp

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.example.grocerylistapp.adapter.ReadOnlyUserItemAdapter
import com.example.grocerylistapp.model.UserItem
import com.google.firebase.firestore.FirebaseFirestore

class UserListDetailFragment : Fragment() {

    private lateinit var listNameText: TextView
    private lateinit var listDateText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReadOnlyUserItemAdapter
    private val userItems = mutableListOf<UserItem>()

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_list_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listNameText = view.findViewById(R.id.detail_list_name)
        listDateText = view.findViewById(R.id.detail_list_date)
        recyclerView = view.findViewById(R.id.detail_items_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ReadOnlyUserItemAdapter(userItems)
        recyclerView.adapter = adapter

        val userId = arguments?.getString("userId")
        val listId = arguments?.getString("listId")

        if (userId == null || listId == null) {
            Toast.makeText(requireContext(), "Missing list data", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .document(userId)
            .collection("groceryLists")
            .document(listId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    listNameText.text = doc.getString("name") ?: "Unnamed List"
                    listDateText.text = doc.getString("date") ?: ""

                    val itemsList = doc.get("items") as? List<*>
                    userItems.clear()
                    if (itemsList != null) {
                        userItems.addAll(itemsList.mapNotNull { item ->
                            val map = item as? Map<*, *> ?: return@mapNotNull null
                            UserItem(
                                name = map["name"] as? String ?: "",
                                quantity = (map["quantity"] as? Number)?.toInt() ?: 0,
                                isChecked = false,
                                imageRes = getImageResIdByName(map["imageName"] as? String)
                            )
                        })
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load list details", Toast.LENGTH_SHORT).show()
            }
    }


    private fun getImageResIdByName(name: String?): Int {
        if (name.isNullOrEmpty()) return com.example.grocerylistapp.R.drawable.bake
        val resId = resources.getIdentifier(name, "drawable", requireContext().packageName)
        return if (resId != 0) resId else com.example.grocerylistapp.R.drawable.bake
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
