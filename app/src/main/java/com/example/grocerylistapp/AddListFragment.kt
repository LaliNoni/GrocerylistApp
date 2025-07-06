package com.example.grocerylistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.grocerylistapp.model.GroceryListModel


class AddListFragment : Fragment() {
    private lateinit var nameInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var addButton: Button

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

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val date = dateInput.text.toString().trim()

            if (name.isEmpty() || date.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both name and date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newList = GroceryListModel(name, date)



            Toast.makeText(requireContext(), "List added!", Toast.LENGTH_SHORT).show()

            nameInput.text.clear()
            dateInput.text.clear()
        }
    }
}