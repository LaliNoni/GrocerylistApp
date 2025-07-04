package com.example.grocerylistapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomePageFragment())
            .commit()

        bottomNavigationView.setOnItemSelectedListener { item ->
           val selectedFragment = when(item.itemId) {
               R.id.nav_grocery -> Grocery_lists_Fragment()
               R.id.nav_search -> SearchFragment()
               R.id.nav_add -> Add_list_Fragment()
               R.id.nav_profile -> ProfileFragment()
               else -> null
            }
            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
                true
            } ?: false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                // TODO: Handle notifications click
                true
            }
            R.id.action_settings -> {
                // TODO: Handle settings click
                true
            }
            R.id.action_logout -> {
                // TODO: Handle logout click
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
