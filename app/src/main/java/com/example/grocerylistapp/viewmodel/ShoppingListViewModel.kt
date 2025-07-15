package com.example.grocerylistapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.grocerylistapp.database.AppLocalDatabase
import com.example.grocerylistapp.database.ShoppingListRoom
import com.example.grocerylistapp.repository.ShoppingListRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShoppingListRepository
    val allShoppingLists: LiveData<List<ShoppingListRoom>>
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    init {
        val dao = AppLocalDatabase.getInstance(application).shoppingListDao()
        repository = ShoppingListRepository(dao)
        allShoppingLists = repository.getAllShoppingLists()
    }

    fun addList(name: String, iconResId: Int, onComplete: (Boolean, String?) -> Unit) {
        repository.addListToFirestoreAndRoom(name, iconResId, onComplete)
    }

    fun insertLists(vararg lists: ShoppingListRoom) {
        viewModelScope.launch {
            repository.insertLists(*lists)
        }
    }

    fun updateShoppingList(list: ShoppingListRoom) {
        viewModelScope.launch {
            repository.updateList(list)
        }
    }

    fun deleteList(list: ShoppingListRoom) {
        viewModelScope.launch {
            repository.deleteList(list)
        }
    }

    fun syncListsFromFirestore(onComplete: (Boolean, String?) -> Unit) {
        repository.syncFromFirebase(viewModelScope, onComplete)
    }
}
