package com.example.myrosecarillon

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Post
import com.example.myrosecarillon.objects.Song
import com.example.myrosecarillon.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile_page.view.*
import kotlinx.android.synthetic.main.my_song_card.view.*
import kotlinx.android.synthetic.main.vote_song_card.view.*
import kotlinx.android.synthetic.main.vote_song_card.view.card_title

class PostViewHolder(itemView: View, val context: Context, val adapter: SongBoardAdapter) : RecyclerView.ViewHolder(itemView){
    private var cardView: CardView = itemView.vote_song_card
    private var upvoteButton: ImageButton = itemView.upvote
    private var downvoteButton: ImageButton = itemView.downvote
    private lateinit var post: Post
    private val auth = FirebaseAuth.getInstance()

    init{
        //sets the upvote and downvote buttons to call the adapter's vote method
        upvoteButton.setOnClickListener {
            Log.d(Constants.TAG, "UPVOTE BUTTON PRESSED")
            adapter.vote(adapterPosition, 1)
        }
        downvoteButton.setOnClickListener {
            Log.d(Constants.TAG, "DOWNVOTE BUTTON PRESSED")
            adapter.vote(adapterPosition, -1)
        }

    }

    fun bind(post: Post){
//        GetSongTask(this).execute(post.songRef)
        this.post = post

        //gets the creator from the post and displays their profile picture on the card
        post.userRef?.get()?.addOnSuccessListener {
            post.user = User.fromSnapshot(it)
            Picasso.get().load(post.user!!.pictureUrl).into(cardView.findViewById<ImageView>(R.id.post_user_profile_image_view))
        }

        cardView.card_title.text = post.song?.title
        val rating = post.votes?.count{it.value == 1}?.minus(post.votes?.count{it.value == -1}!!)
        cardView.rating.text = rating.toString()
        cardView.save_song.setOnClickListener { cardView.post_composer.play() }

        //determines if the user has voted on this post and highlights the corresponding vote button
        when (post.votes?.get(auth.currentUser?.uid)){
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

        //sends the midi to the composer view to display its notes
        post.song?.midi?.let { cardView.post_composer.sendMidi(it) }
    }
}
