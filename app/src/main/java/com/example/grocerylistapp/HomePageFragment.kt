package com.example.grocerylistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class HomePageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createAccountButton = view.findViewById<Button>(R.id.create_account_button)
        val loginTextView = view.findViewById<TextView>(R.id.text_login)

        createAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_registerFragment)
        }

        loginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_logInFragment)
        }

    }
}
