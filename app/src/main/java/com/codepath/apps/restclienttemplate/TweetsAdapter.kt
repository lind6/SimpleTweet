package com.codepath.apps.restclienttemplate

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.models.Tweet

private const val TAG = "TweetsAdapter"

class TweetsAdapter( val tweets: ArrayList<Tweet> ) : RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ): TweetsAdapter.ViewHolder {
        val context  = parent.context
        val inflater = LayoutInflater.from( context )

        // inflate the item layout
        val view = inflater.inflate( R.layout.item_tweet, parent, false )

        return ViewHolder( view )
    }

    // populate item data via viewholder
    override fun onBindViewHolder( holder: TweetsAdapter.ViewHolder, position: Int ) {

        // get data model from position (index) in the view
        val tweet: Tweet = tweets.get( position )

        holder.tvUsername.text  = tweet.user?.name
        holder.tvTweetBody.text = tweet.body
        holder.tvTimestamp.text = tweet.createdAt

        Log.i( TAG, "timestamp: " + holder.tvTimestamp.text )

        Glide.with( holder.itemView ).load( tweet.user?.publicImageURL ).into( holder.ivProfileImage )
    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    // Clean all elements of the recycler
    fun clear() {
        tweets.clear()
        notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll( tweetList: List<Tweet> ) {
        tweets.addAll( tweetList )
        notifyDataSetChanged()
    }

    class ViewHolder( itemView: View) : RecyclerView.ViewHolder( itemView ) {
        val ivProfileImage = itemView.findViewById<ImageView>( R.id.ivProfileImage )
        val tvUsername     = itemView.findViewById<TextView>(  R.id.tvUsername     )
        val tvTweetBody    = itemView.findViewById<TextView>(  R.id.tvTweetBody    )
        val tvTimestamp    = itemView.findViewById<TextView>(  R.id.tvTimestamp    )
    }

}