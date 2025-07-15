package com.example.grocerylistapp.repository

import com.example.grocerylistapp.database.UserProfile
import com.example.grocerylistapp.database.UserProfileDao

class UserProfileRepository(private val dao: UserProfileDao) {

    fun getUserProfile(email: String) = dao.getUserByEmail(email)

    suspend fun saveUserProfile(profile: UserProfile) = dao.insert(profile)

    suspend fun clearUserProfile() = dao.clear()
}