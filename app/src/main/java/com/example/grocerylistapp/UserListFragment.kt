package com.example.grocerylistapp

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.adapter.UsersViewGroceryListsAdapter
import com.example.grocerylistapp.model.GroceryListModel
import com.google.firebase.firestore.FirebaseFirestore

class UserListFragment : Fragment() {

    private lateinit var headerTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsersViewGroceryListsAdapter
    private val groceryLists = mutableListOf<GroceryListModel>()

    private val firestore = FirebaseFirestore.getInstance()

    private var userId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search_user_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        headerTextView = view.findViewById(R.id.user_lists_header)

        val userName = arguments?.getString("userName")
        if (!userName.isNullOrEmpty()) {
            val title = getString(R.string.user_lists_description, userName)
            headerTextView.text = title
        }

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userId = arguments?.getString("userId")
        if (userId == null) {
            Toast.makeText(requireContext(), "User ID missing", Toast.LENGTH_SHORT).show()
            return
        }

        recyclerView = view.findViewById(R.id.user_lists_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = UsersViewGroceryListsAdapter(groceryLists) { list ->
            val bundle = Bundle().apply {
                putString("userId", userId)
                putString("listId", list.id)
            }
            findNavController().navigate(R.id.action_userListFragment_to_userListDetailFragment, bundle)
        }
        recyclerView.adapter = adapter

        loadUserLists(userId!!)
    }

    private fun loadUserLists(userId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("groceryLists")
            .get()
            .addOnSuccessListener { result ->
                groceryLists.clear()
                for (doc in result) {
                    val list = GroceryListModel(
                        id = doc.id,
                        name = doc.getString("name") ?: "Unnamed",
                        date = doc.getString("date") ?: ""
                    )
                    groceryLists.add(list)
                }
                if (groceryLists.isEmpty()) {
                    Toast.makeText(requireContext(), "No lists found for this user", Toast.LENGTH_SHORT).show()
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load lists: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigate(R.id.action_userListFragment_to_searchFragment3)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}
