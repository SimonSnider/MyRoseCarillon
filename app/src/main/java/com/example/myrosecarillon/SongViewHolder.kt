package com.example.myrosecarillon

import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myrosecarillon.objects.Song
import kotlinx.android.synthetic.main.my_song_card.view.*
import kotlinx.android.synthetic.main.vote_song_card.view.card_title

class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private var cardView: CardView = itemView.my_song_card

    fun bind(song: Song){
        cardView.card_title.text = song.title
    }
}