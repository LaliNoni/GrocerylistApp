package com.example.grocerylistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.adapter.UserItemAdapter
import com.example.grocerylistapp.model.UserItem

class ItemUserInputFragment : Fragment() {

    private lateinit var userItemRecyclerView: RecyclerView
    private lateinit var addItemButton: Button
    private val userItems = mutableListOf<UserItem>()
    private lateinit var adapter: UserItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_user_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userItemRecyclerView = view.findViewById(R.id.userItemRecyclerView)
        addItemButton = view.findViewById(R.id.add_item_button)

        userItems.add(UserItem())
        userItems.add(UserItem())

        adapter = UserItemAdapter(
            userItems,
            onAddItemBelow = { position ->
                userItems.add(position + 1, UserItem())
                adapter.notifyItemInserted(position + 1)
            },
            onImageClick = { position ->
                // Handle image click here, e.g., open image picker dialog
                Toast.makeText(requireContext(), "Image clicked at position $position", Toast.LENGTH_SHORT).show()
            }
        )

        userItemRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userItemRecyclerView.adapter = adapter

        addItemButton.setOnClickListener {
            userItems.add(UserItem())
            adapter.notifyItemInserted(userItems.size - 1)
        }
    }
}
