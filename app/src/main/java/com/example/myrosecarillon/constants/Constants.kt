package com.example.myrosecarillon.constants

import com.google.firebase.firestore.FirebaseFirestore

object Constants {
    const val DEFAULT_PICTURE_PATH = "https://firebasestorage.googleapis.com/v0/b/myrosecarillon.appspot.com/o/Pictures%2Fdefault%20pic.jpg?alt=media&token=56e037ec-f480-4d41-9203-3b566b55677d"
    const val TAG = "MRC"
    const val SONGS_PATH = "Songs"
    const val USERS_PATH = "Users"
    const val POSTS_PATH = "Posts"
    const val MIDI_SUFFIX = ".mid"
    val userRef = FirebaseFirestore.getInstance().collection(USERS_PATH)
    val songsRef = FirebaseFirestore.getInstance().collection(SONGS_PATH)
    val postsRef = FirebaseFirestore.getInstance().collection(POSTS_PATH)
}