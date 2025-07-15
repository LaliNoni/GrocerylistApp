package com.example.grocerylistapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import com.example.grocerylistapp.database.UserProfile
import com.example.grocerylistapp.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfileViewModel(private val repository: UserProfileRepository) : ViewModel() {

    fun getUser(email: String): LiveData<UserProfile?> {
        return repository.getUserProfile(email)
    }

    fun insertOrUpdateUser(user: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveUserProfile(user)
        }
    }
}
