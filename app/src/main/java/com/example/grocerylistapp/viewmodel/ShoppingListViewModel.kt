package com.example.grocerylistapp.viewmodel

import androidx.lifecycle.*
import com.example.grocerylistapp.database.ShoppingListRoom
import com.example.grocerylistapp.repository.ShoppingListRepository
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    val allShoppingLists: LiveData<List<ShoppingListRoom>> = repository.allShoppingLists

    fun insert(list: ShoppingListRoom) = viewModelScope.launch {
        repository.insert(list)
    }

    fun delete(list: ShoppingListRoom) = viewModelScope.launch {
        repository.delete(list)
    }
}
