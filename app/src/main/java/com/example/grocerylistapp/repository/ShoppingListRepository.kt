package com.example.grocerylistapp.repository

import androidx.lifecycle.LiveData
import com.example.grocerylistapp.database.ShoppingListDao
import com.example.grocerylistapp.database.ShoppingListRoom

class ShoppingListRepository(private val shoppingListDao: ShoppingListDao) {

    val allShoppingLists: LiveData<List<ShoppingListRoom>> = shoppingListDao.getAll()

    suspend fun insert(list: ShoppingListRoom) {
        shoppingListDao.insertAll(list)
    }

    suspend fun delete(list: ShoppingListRoom) {
        shoppingListDao.delete(list)
    }
}