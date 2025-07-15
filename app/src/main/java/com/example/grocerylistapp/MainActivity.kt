package com.example.grocerylistapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.grocerylistapp.database.AppLocalDatabase
import com.example.grocerylistapp.database.ShoppingListRoom
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(
            if (auth.currentUser != null) R.id.groceryListsFragment
            else R.id.homePageFragment
        )
        navController.graph = navGraph

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigationView.visibility = when (destination.id) {
                R.id.homePageFragment,
                R.id.logInFragment,
                R.id.registerFragment -> View.GONE
                else -> View.VISIBLE
            }

            supportActionBar?.setDisplayHomeAsUpEnabled(
                destination.id == R.id.userListFragment ||
                        destination.id == R.id.userListDetailFragment
            )
        }

        if (auth.currentUser != null) {
            syncGroceryListsFromFirestore()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                auth.signOut()
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.homePageFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun syncGroceryListsFromFirestore() {
        val currentUser = auth.currentUser ?: return
        val db = AppLocalDatabase.getInstance(this)

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("groceryLists")
            .get()
            .addOnSuccessListener { documents ->
                val lists = documents.map { doc ->
                    ShoppingListRoom(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        date = doc.getString("date") ?: "",
                        imageResId = R.drawable.bake, // Default image or map from imageName if needed
                        isDone = doc.getBoolean("isDone") ?: false,
                        lastUpdated = System.currentTimeMillis()
                    )
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    db.shoppingListDao().deleteAll()
                    db.shoppingListDao().insertLists(lists)
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    db.shoppingListDao().insertLists(lists)
                }
            }
    }
}
