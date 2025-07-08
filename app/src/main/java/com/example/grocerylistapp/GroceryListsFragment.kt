package com.example.grocerylistapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.adapter.GroceryListAdapter
import com.example.grocerylistapp.model.GroceryListModel

class GroceryListsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroceryListAdapter
    private val groceryLists = mutableListOf<GroceryListModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_grocery_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.groceryRecyclerView)

        adapter = GroceryListAdapter(groceryLists,
            onCheckedChange = { position, isChecked ->
                groceryLists[position].isDone = isChecked
                // Save change to DB here later
            },
            onDeleteClick = { position ->
                groceryLists.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        groceryLists.add(GroceryListModel("Weekly Groceries", "10 May 2025"))
        groceryLists.add(GroceryListModel("Party Prep", "12 APR 2025", isDone = true))

        adapter.notifyDataSetChanged()
    }
}

