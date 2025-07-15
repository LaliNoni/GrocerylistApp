package com.example.grocerylistapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItemRoom(
    @PrimaryKey val id: String,
    val listId: String, // foreign key to ShoppingListRoom
    val name: String,
    val quantity: Int,
    val isChecked: Boolean
)