package com.example.grocerylistapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shopping_lists")
    fun getAll(): LiveData<List<ShoppingListRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg lists: ShoppingListRoom)

    @Delete
    suspend fun delete(list: ShoppingListRoom)
}
