package com.example.grocerylistapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_lists")
data class ShoppingListRoom(
    @PrimaryKey val id: String,
    val name: String,
    val iconResId: Int,
    val lastUpdated: Long
)
