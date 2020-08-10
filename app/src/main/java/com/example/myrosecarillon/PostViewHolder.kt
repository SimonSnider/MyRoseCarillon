package com.example.myrosecarillon

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Post
import com.example.myrosecarillon.objects.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.vote_song_card.view.*
import kotlinx.android.synthetic.main.vote_song_card.view.card_title

class PostViewHolder(itemView: View, val context: Context, val adapter: SongBoardAdapter) : RecyclerView.ViewHolder(itemView), GetSongTask.SongConsumer {
    private var cardView: CardView = itemView.vote_song_card
    private var upvoteButton: ImageButton = itemView.upvote
    private var downvoteButton: ImageButton = itemView.downvote
    private lateinit var post: Post
    private val auth = FirebaseAuth.getInstance()

    init{
        upvoteButton.setOnClickListener {
            Log.d(Constants.TAG, "UPVOTE BUTTON PRESSED")
            adapter.vote(adapterPosition, 1)
//            upvoteButton.setColorFilter(context.resources.getColor(android.R.color.holo_red_dark))
//            downvoteButton.colorFilter = null
        }
        downvoteButton.setOnClickListener {
            Log.d(Constants.TAG, "DOWNVOTE BUTTON PRESSED")
            adapter.vote(adapterPosition, -1)
//            downvoteButton.setColorFilter(context.resources.getColor(android.R.color.holo_red_dark))
//            upvoteButton.colorFilter = null
        }

    }

    fun bind(post: Post){
//        GetSongTask(this).execute(post.songRef)
        this.post = post
        cardView.card_title.text = post.song?.title
        val rating = post.votes?.count{it.value == 1}?.minus(post.votes?.count{it.value == -1}!!)
        cardView.rating.text = rating.toString()
        when (post.votes?.get("MRXkKWXrxMDwkDL77qoi")){
            1 -> {
                upvoteButton.setColorFilter(context.resources.getColor(android.R.color.holo_red_dark))
                downvoteButton.colorFilter = null
            }
            -1 -> {
                downvoteButton.setColorFilter(context.resources.getColor(android.R.color.holo_red_dark))
                upvoteButton.colorFilter = null
            }
            else -> {
                downvoteButton.colorFilter = null
                upvoteButton.colorFilter = null
            }
        }
    }

    override fun onSongLoaded(song: Song?) {
        cardView.card_title.text = song?.title
    }
}
