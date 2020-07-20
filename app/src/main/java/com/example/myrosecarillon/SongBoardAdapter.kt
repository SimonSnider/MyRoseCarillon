package com.example.myrosecarillon

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Post
import com.google.firebase.firestore.*

class SongBoardAdapter(val context: Context):  RecyclerView.Adapter<PostViewHolder>(){

    private val posts = ArrayList<Post>()
    private val postsRef = FirebaseFirestore.getInstance().collection(Constants.POSTS_PATH)
    private lateinit var listenerRegistration: ListenerRegistration

    fun addSnapshotListener() {
        listenerRegistration = postsRef
            .orderBy(Post.RATING_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener{querySnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    processSnapshotChanges(querySnapshot!!)
                }
            }
    }

    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
        for (documentChange in querySnapshot.documentChanges) {
            val post = Post.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $post")
                    posts.add(0, post)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removice $post")
                    val index = posts.indexOfFirst{it.id == post.id}
                    posts.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $post")
                    val index = posts.indexOfFirst { it.id == post.id }
                    posts[index] = post
                    notifyItemChanged(index)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.vote_song_card, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }
}