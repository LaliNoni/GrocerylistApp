package com.example.grocerylistapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShoppingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ShoppingListRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLists(lists: List<ShoppingListRoom>)

    @Query("DELETE FROM shopping_lists")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(list: ShoppingListRoom)

    @Query("SELECT * FROM shopping_lists ORDER BY lastUpdated DESC")
    fun getAll(): LiveData<List<ShoppingListRoom>>
}

