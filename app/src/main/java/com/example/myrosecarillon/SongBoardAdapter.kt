package com.example.myrosecarillon

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Post
import com.example.myrosecarillon.objects.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class SongBoardAdapter(val context: Context):  RecyclerView.Adapter<PostViewHolder>(){

    private val posts = ArrayList<Post>()
    private val postsRef = FirebaseFirestore.getInstance().collection(Constants.POSTS_PATH)
    private val songsRef = FirebaseFirestore.getInstance().collection(Constants.SONGS_PATH)
    private val auth = FirebaseAuth.getInstance()
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
            Log.d(Constants.TAG, "votes: ${post.votes}")
            Log.d(Constants.TAG, "user vote: ${post.votes?.get("MRXkKWXrxMDwkDL77qoi")}")
//            songsRef.document("pAO7xNyb3QtrbxNWMXEM").get().addOnCompleteListener() { task ->
//                post.song = task.result?.let { it1 -> Song.fromSnapshot(it1) }
//                val index = posts.indexOfFirst { it.id == post.id }
//                notifyItemChanged(index)
//            }
            post.songRef?.get()?.addOnCompleteListener() {task ->
                post.song = task.result?.let { it1 -> Song.fromSnapshot(it1) }
                val index = posts.indexOfFirst { it.id == post.id }
                if (index > -1 ) notifyItemChanged(index)
            }
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $post")
                    posts.add(0, post)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $post")
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
        return PostViewHolder(view, context, this)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    fun vote(position: Int, num: Int){
        val post = posts[position]
        if(post.votes?.get("MRXkKWXrxMDwkDL77qoi") == num){
            post.votes?.put("MRXkKWXrxMDwkDL77qoi", 0)
        } else post.votes?.put("MRXkKWXrxMDwkDL77qoi", num)
        val upvotes = post.votes?.count{it.value == 1}
        val downvotes = post.votes?.count{it.value == -1}
        post.likes = upvotes!!
        post.dislikes = downvotes!!
        post.rating = upvotes - downvotes
        postsRef.document(post.id).set(post)
    }
}