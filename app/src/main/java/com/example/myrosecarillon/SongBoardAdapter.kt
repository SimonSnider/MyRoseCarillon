package com.example.myrosecarillon

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Post
import com.example.myrosecarillon.objects.Song
import com.example.myrosecarillon.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class SongBoardAdapter(val context: Context):  RecyclerView.Adapter<PostViewHolder>(){

    private val posts = ArrayList<Post>()
    private val postsRef = Constants.postsRef
    private val auth = FirebaseAuth.getInstance()
    private lateinit var listenerRegistration: ListenerRegistration

    //creates a snapshot listener for all the posts in the database
    fun addSnapshotListener() {
        listenerRegistration = postsRef
            .orderBy(Post.RATING_KEY, Query.Direction.DESCENDING)
            .addSnapshotListener{querySnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    processSnapshotChanges(querySnapshot!!)
                }
            }
    }

    //processes the changes in the snapshot listener.
    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
        for (documentChange in querySnapshot.documentChanges) {
            val post = Post.fromSnapshot(documentChange.document)

            //gets the song from the document reference and adds it to the post
            post.songRef?.get()?.addOnCompleteListener() {task ->
                post.song = task.result?.let { it1 -> Song.fromSnapshot(it1) }
                val index = posts.indexOfFirst { it.id == post.id }
                if (index > -1 ) notifyItemChanged(index)
            }
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding ${post.id}")
                    posts.add(itemCount, post)
                    notifyItemInserted(itemCount)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing ${post.id}")
                    val index = posts.indexOfFirst{it.id == post.id}
                    posts.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying ${post.id}")
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

    //handles upvoting or downvoting a post. If num is 1, the post was upvoted, if num is -1, the post was downvoted
    fun vote(position: Int, num: Int){
        val post = posts[position]
        val previous = post.votes?.get(auth.currentUser?.uid)

        //if the user is removing their previous vote, replace it with a 0 to indicate no vote
        if(previous == num){
            auth.currentUser?.uid?.let { post.votes?.put(it, 0) }
        } else auth.currentUser?.uid?.let { post.votes?.put(it, num) }

        //counts the number of upvotes and downvotes on the post and updates its total upvotes, downvotes, and rating
        val upvotes = post.votes?.count{it.value == 1} ?: 0
        val downvotes = post.votes?.count{it.value == -1} ?: 0
        post.likes = upvotes
        post.dislikes = downvotes
        post.rating = upvotes - downvotes

        //updates the post's firebase document
        postsRef.document(post.id).set(post)

        //increments or decrements upvotesReceived for the post creator
        post.userRef?.get()?.addOnSuccessListener {
            val user = User.fromSnapshot(it)
            user.getUpvote(previous, num)
        }
    }

    //checks whether or not a song exists in the queue already
    fun checkForSong(id: String?): Boolean {
        for(post in posts){
            if (post.song?.id == id){
                return true
            }
        }
        return false
    }
}