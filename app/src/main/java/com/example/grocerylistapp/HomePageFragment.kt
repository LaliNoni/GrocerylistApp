package com.example.grocerylistapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomePageFragment : Fragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_grocery -> {
                    // TODO: Navigate to Grocery screen
                    true
                }
                R.id.nav_search -> {
                    // TODO: Navigate to Search screen
                    true
                }
                R.id.nav_add -> {
                    // TODO: Navigate to Add screen
                    true
                }
                R.id.nav_profile -> {
                    // TODO: Navigate to Profile screen
                    true
                }
                else -> false
            }
        }
    }
}
