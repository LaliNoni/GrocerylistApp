package com.example.grocerylistapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_lists")
data class ShoppingListRoom(
    @PrimaryKey val id: String,
    val name: String,
    val date: String,
    val imageResId: Int,
    val lastUpdated: Long,
    var isDone: Boolean = false
)
