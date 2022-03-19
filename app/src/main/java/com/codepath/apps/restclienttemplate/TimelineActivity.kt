package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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

    override fun onCreateOptionsMenu( menu: Menu? ) : Boolean {
        menuInflater.inflate( R.menu.menu_main, menu )
        return true
    }

    override fun onOptionsItemSelected( item: MenuItem ): Boolean {
        if ( item.itemId == R.id.compose ) {
            Toast.makeText( this, "Ready to compose tweet!", Toast.LENGTH_SHORT ).show()
            val intent = Intent( this, ComposeActivity::class.java )
            startActivityForResult( intent, REQUEST_CODE )
        }
        return super.onOptionsItemSelected( item )
    }

    // executes when returning from composeActivity
    // NOTE: this method is deprecated in Android.  See codepath article on current method to do this task:
    // https://guides.codepath.org/android/Using-Intents-to-Create-Flows#returning-data-result-to-parent-activity
    override fun onActivityResult( requestCode: Int, resultCode: Int, data: Intent? ) {

        if ( resultCode == RESULT_OK && requestCode == REQUEST_CODE ) {
            // get data from intent
            val tweet = data?.getParcelableExtra<Tweet>( "tweet" ) as Tweet

            // update timeline
            // modify data source of tweets
            tweets.add( 0, tweet )

            // update adapter
            adapter.notifyItemInserted( 0 )

            // scroll to newly inserted tweet
            rvTweets.smoothScrollToPosition(0 )
        }
        super.onActivityResult( requestCode, resultCode, data )
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
        const val REQUEST_CODE = 20
    }
}