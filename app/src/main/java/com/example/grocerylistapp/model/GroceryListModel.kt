package com.example.grocerylistapp.model

data class GroceryListModel(
    val id: String = "",
    val name: String = "",
    val date: String = "",
    val userId: String = "",
    val items: List<UserItem> = emptyList(),
    var isDone: Boolean = false
)