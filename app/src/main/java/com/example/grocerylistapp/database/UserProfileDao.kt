package com.example.grocerylistapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): LiveData<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfile)

    @Query("DELETE FROM user_profile")
    suspend fun clear()
}

