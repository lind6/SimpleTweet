package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

private const val TAG = "AppComposeActivity"
private const val ERROR_MSG = "Character count exceeds limit of 280"
private const val MAX_CHARS = 280

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var client: TwitterClient
    lateinit var charCount: EditText


    override fun onCreate( savedInstanceState: Bundle? ) {

        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_compose )

        var charCount = findViewById<TextView>( R.id.tvCount )
        etCompose     = findViewById( R.id.etTweetCompose )
        btnTweet      = findViewById( R.id.btnTweet       )

        etCompose.addTextChangedListener( object: TextWatcher {

            // executes when text is changed (while supplying the range of text)
            override fun onTextChanged( s: CharSequence?, start: Int, before: Int, count: Int ) {
//                Log.i( TAG, "--- onTextChanged() ---" )

                // get number of characters in the text
                val nbChars = etCompose.text.toString().length

                // update tvCount textView to show current character count
                if ( nbChars > MAX_CHARS ) {
//                    Log.i( TAG, "Too many characters: $nbChars" )
                    charCount.setText( ERROR_MSG )
                    charCount.setTextColor( Color.rgb( 200, 0, 0 ))
                } else {
                    charCount = findViewById( R.id.tvCount )
                    charCount.setText( "Characters: $nbChars" )
                    charCount.setTextColor( Color.rgb( 128, 128, 128 ) )
                }
            }

            // executes before text is modified
            override fun beforeTextChanged( s: CharSequence, start: Int, count: Int, after: Int ) {
//                Log.i( TAG, "--- beforeTextChanged() ---" )

                // turn off submit button until user enters a valid message
                btnTweet.setEnabled( false )

                // Initialize character counter to zero.
                charCount.setText( "Characters: 0" )
                charCount.setTextColor( Color.rgb( 128, 128, 128 )) // middle grey
            }

            // executes immediately after text has been modified
            override fun afterTextChanged( s: Editable) {
//                Log.i( TAG, "--- afterTextChanged() ---" )

                val nbChars = s.toString().length

                if ( nbChars > 0 && nbChars <= MAX_CHARS ) {
                    // valid message
                    btnTweet.setEnabled( true )
                } else {
                    // empty message, or message too long
                    btnTweet.setEnabled( false )
                }
            }
        })


        btnTweet  = findViewById( R.id.btnTweet       )

        client = TwitterApplication.getRestClient( this )

        // handle event when clicking 'tweet' button
        btnTweet.setOnClickListener {

            // grab content of edit text
            val tweetContent = etCompose.text.toString()

            // validate tweet
            if ( tweetContent.isEmpty() ) {
                Toast.makeText( this, "Empty tweets not allowed", Toast.LENGTH_SHORT ).show()
            } else if ( tweetContent.length > 140 ) {
                Toast.makeText( this, "Tweet exceeds 140 characters", Toast.LENGTH_SHORT ).show()
            } else {
                // make API call to twitter to publish the tweet
                Toast.makeText( this, tweetContent, Toast.LENGTH_SHORT ).show()
                client.publishTweet( tweetContent, object: JsonHttpResponseHandler() {

                    override fun onSuccess( statusCode: Int, headers: Headers?, json: JSON ) {
                        // send tweet back to timelineActivity to show
                        Log.i( TAG, "Successfully published tweet" )
                        val tweet = Tweet.fromJson( json.jsonObject )

                        val intent = Intent()
                        intent.putExtra( "tweet", tweet )
                        setResult( RESULT_OK, intent )
                        finish()
                    }

                    override fun onFailure(
                        statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?
                    ) {
                        Log.e( TAG, "Failed to publish tweet", throwable )
                    }
                } )
            }
        }
    }
}