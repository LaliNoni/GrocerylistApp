package com.example.grocerylistapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val email: String,
    val name: String,
    val lastName: String,
    val profileImageRes: Int
)