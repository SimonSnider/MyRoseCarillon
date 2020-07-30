package com.example.myrosecarillon

import android.util.Log
import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Post
import com.example.myrosecarillon.objects.Song
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.vote_song_card.view.*
import kotlinx.android.synthetic.main.vote_song_card.view.card_title

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), GetSongTask.SongConsumer {
    private var cardView: CardView = itemView.vote_song_card
    private val songsRef = FirebaseFirestore.getInstance().collection(Constants.SONGS_PATH)

    fun bind(post: Post){
        GetSongTask(this).execute(post.songRef)
        val rating = post.likes - post.dislikes
        cardView.rating.text = rating.toString()
    }

    override fun onSongLoaded(song: Song?) {
        cardView.card_title.text = song?.title
    }
}
