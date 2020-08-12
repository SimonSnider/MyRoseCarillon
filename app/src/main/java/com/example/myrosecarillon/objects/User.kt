package com.example.myrosecarillon.objects

import android.graphics.Bitmap
import com.example.myrosecarillon.constants.Constants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

class User(var pictureUrl: String = Constants.DEFAULT_PICTURE_PATH, var displayName: String = "", var carills: Int = 0, var darkMode: Boolean = false) {
    @get:Exclude var id = ""

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): User {
            val user = snapshot.toObject(User::class.java)!!
            user.id = snapshot.id
            return user
        }
    }
}