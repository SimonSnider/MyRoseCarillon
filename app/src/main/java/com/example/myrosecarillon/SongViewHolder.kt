package com.example.myrosecarillon

import android.app.AlertDialog
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Song
import kotlinx.android.synthetic.main.my_song_card.view.*
import kotlinx.android.synthetic.main.vote_song_card.view.card_title

class SongViewHolder(itemView: View, val adapter: MySongsAdapter) : RecyclerView.ViewHolder(itemView){
    private var cardView: CardView = itemView.my_song_card

    fun bind(song: Song){
        cardView.card_title.text = song.title
        cardView.edit_song.setOnClickListener {
            //TODO: Navigate to compose fragment with this song's midi
            Log.d(Constants.TAG, "Editing song ${song.id}")
        }
        cardView.play_song.setOnClickListener {
            cardView.song_card_display.play()
            Log.d(Constants.TAG, "Playing song ${song.id}")
        }
        cardView.song_card_display.sendMidi(song.midi)
        cardView.delete_song.setOnClickListener{
            adapter.showDeleteDialog(adapterPosition)
        }
    }
}