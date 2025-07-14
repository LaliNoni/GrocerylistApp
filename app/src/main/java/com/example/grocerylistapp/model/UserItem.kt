package com.example.grocerylistapp.model

import com.example.grocerylistapp.R

data class UserItem(
    var name: String = "",
    var quantity: Int = 0,
    var isChecked: Boolean = false,
    var imageRes: Int = 0
)