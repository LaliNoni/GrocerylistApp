package com.example.grocerylistapp.repository

import androidx.lifecycle.LiveData
import com.example.grocerylistapp.database.ShoppingListDao
import com.example.grocerylistapp.database.ShoppingListRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShoppingListRepository(private val dao: ShoppingListDao) {

    fun getAllShoppingLists(): LiveData<List<ShoppingListRoom>> {
        return dao.getAll()
    }

    suspend fun insertAll(vararg lists: ShoppingListRoom) {
        dao.insertAll(*lists)
    }

    suspend fun updateList(list: ShoppingListRoom) {
        dao.insertAll(list)
    }

    suspend fun deleteList(list: ShoppingListRoom) {
        dao.delete(list)
    }

    fun syncFromFirebase(scope: CoroutineScope, onComplete: (Boolean, String?) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            onComplete(false, "User not logged in.")
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("shoppingLists")
            .get()
            .addOnSuccessListener { result ->
                val localLists = result.map { doc ->
                    ShoppingListRoom(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        date = doc.getString("date") ?: "",
                        iconResId = (doc.getLong("iconResId") ?: 0L).toInt(),
                        lastUpdated = doc.getLong("lastUpdated") ?: System.currentTimeMillis()
                    )
                }

                scope.launch {
                    withContext(Dispatchers.IO) {
                        insertAll(*localLists.toTypedArray())
                    }
                    onComplete(true, null)
                }
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message)
            }
    }

    fun addListToFirestoreAndRoom(
        name: String,
        iconResId: Int,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            onComplete(false, "User not logged in")
            return
        }

        val listData = hashMapOf(
            "name" to name,
            "iconResId" to iconResId,
            "lastUpdated" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("shoppingLists")
            .add(listData)
            .addOnSuccessListener { docRef ->
                val localList = ShoppingListRoom(
                    id = docRef.id,
                    name = name,
                    date = "",
                    iconResId = iconResId,
                    lastUpdated = System.currentTimeMillis()
                )

                CoroutineScope(Dispatchers.IO).launch {
                    dao.insertAll(localList)
                }

                onComplete(true, null)
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message)
            }
    }
}
