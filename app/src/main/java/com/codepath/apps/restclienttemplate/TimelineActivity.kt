package com.codepath.apps.restclienttemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient
    lateinit var rvTweets: RecyclerView
    lateinit var adapter: TweetsAdapter
    val tweets = ArrayList<Tweet>()

    lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreate( savedInstanceState: Bundle? ) {

        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_timeline )

        // get twitter context from REST API
        client = TwitterApplication.getRestClient( this )

        // get parent of the swipe mechanism
        swipeContainer = findViewById( R.id.swipeContainer )

        swipeContainer.setOnRefreshListener {
            Log.i( TAG, "swipe.onRefreshListener() - refreshing timeline" )
            populateHomeTimeline()
        }

        // configure refresh colors
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        rvTweets = findViewById( R.id.rvTweets )
        adapter  = TweetsAdapter( tweets )

        rvTweets.layoutManager = LinearLayoutManager( this )
        rvTweets.adapter       = adapter

        populateHomeTimeline()
    }

    fun populateHomeTimeline() {

        client.getHomeTimeline( object: JsonHttpResponseHandler() {

            override fun onSuccess( statusCode: Int, headers: Headers, json: JSON ) {
                Log.i( TAG, "onSuccess! $json" )
                val jsonArray = json.jsonArray

                try {
                    // clear timeline of existing tweets
                    adapter.clear()

                    val tweetsInBuffer = Tweet.fromJsonArray( jsonArray )
                    tweets.addAll( tweetsInBuffer )
                    adapter.notifyDataSetChanged()

                    // tell android to stop showing the refresh icon (i.e. we've completed the task)
                    swipeContainer.setRefreshing( false )

                } catch( e: JSONException) {
                    Log.e( TAG, "JSON Exception: $e")
                }
            }

            override fun onFailure( statusCode: Int, headers: Headers?, response: String?, throwable: Throwable? ) {
                Log.i( TAG, "onFailure $statusCode")
            }
        })
    }

    companion object {
        const val TAG = "TimeLineActivity"
    }
}