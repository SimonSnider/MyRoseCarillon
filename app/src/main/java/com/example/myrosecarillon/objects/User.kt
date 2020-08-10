package com.example.myrosecarillon.objects

import android.graphics.Bitmap
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

class User(var pictureUrl: String = "", var displayName: String = "", var username: String = "", var carills: Int = 0, var darkMode: Boolean = false) {
    @get:Exclude var id = ""

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): User {
            val user = snapshot.toObject(User::class.java)!!
            user.id = snapshot.id
            return user
        }
    }
}