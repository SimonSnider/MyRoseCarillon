package com.example.myrosecarillon

import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myrosecarillon.objects.Post
import kotlinx.android.synthetic.main.vote_song_card.view.*
import kotlinx.android.synthetic.main.vote_song_card.view.card_title

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var cardView: CardView = itemView.vote_song_card

    fun bind(post: Post){
        cardView.card_title.text = post.song?.title
        val rating = post.likes - post.dislikes
        cardView.rating.text = rating.toString()
    }
}
