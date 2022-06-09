package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.utilities.DateUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {

    public String body;
    public String createdAt;
    public User user;
    public String imageUrl;
    public String timeStamp;
    public String id;
    public int retweetCount;
    public int likeCount;
    public boolean favorited;

    // Empty constructor needed by Parceler library
    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.imageUrl = "";
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));

        if (jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }

        if(jsonObject.getJSONObject("entities").has("media")) {
            JSONArray media = jsonObject.getJSONObject("entities").getJSONArray("media");
            if (media.length() > 0){
                tweet.imageUrl = media.getJSONObject(0).getString("media_url").toString();
            }
        } else {
            tweet.imageUrl = null;
        }

        tweet.timeStamp = DateUtility.getRelativeTimeAgo(tweet.getCreatedAt());
        tweet.id = jsonObject.getString("id");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.likeCount = jsonObject.getInt("favorite_count");
        tweet.favorited = jsonObject.getBoolean("favorited");
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
