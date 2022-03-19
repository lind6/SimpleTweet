package com.codepath.apps.restclienttemplate.models

import android.os.Parcelable
import com.codepath.apps.restclienttemplate.TimeFormatter
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
class Tweet( var body: String = "", var createdAt: String = "", var user: User? = null ):
    Parcelable {

    companion object {

        fun fromJson( jsonObject: JSONObject): Tweet {

            var timeStamp = ""
            var str = ""
            val tweet = Tweet()

            tweet.body = jsonObject.getString( "text"       )
            tweet.user = User.fromJson( jsonObject.getJSONObject( "user" ) )

            // tweet.createdAt = jsonObject.getString( "created_at" )
            str = jsonObject.getString( "created_at" )
            tweet.createdAt = getFormattedTimestamp( str )

            return tweet
        }

        fun fromJsonArray( jsonArray: JSONArray) : List<Tweet> {

            val tweets = ArrayList<Tweet>()

            for ( i in 0 until jsonArray.length() ) {
                tweets.add( fromJson( jsonArray.getJSONObject(i) ) )
            }

            return tweets
        }

        // return difference between current time and createdAt timestamps
        // as formatted string matching Twitter API
        fun getFormattedTimestamp( timeStamp: String ) : String {
            return TimeFormatter.getTimeDifference( timeStamp )
        }
    }
}