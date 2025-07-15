package com.example.grocerylistapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShoppingItemDao {

    @Query("SELECT * FROM shopping_items WHERE listId = :listId")
    fun getItemsForList(listId: String): LiveData<List<ShoppingItemRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(vararg items: ShoppingItemRoom)

    @Delete
    suspend fun deleteItem(item: ShoppingItemRoom)

    @Query("DELETE FROM shopping_items WHERE listId = :listId")
    suspend fun deleteItemsForList(listId: String)
}
