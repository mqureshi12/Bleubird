package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {

    TwitterClient client = TwitterApp.getRestClient(this);
    Tweet tweet;
    ImageView ivProfileImage;
    ImageView ivTweetImage;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvTimeStamp;
    TextView tvName;
    TextView tvRetweetCount;
    TextView tvLikeCount;
//    ImageButton btnReply;
    ImageButton ibLikeEmpty;
    ImageButton ibLike;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivTweetImage = findViewById(R.id.ivMedia);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvName = findViewById(R.id.tvName);
        tvBody = findViewById(R.id.tvBody);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);

        tvRetweetCount = findViewById(R.id.tvRetweetCount);
        tvLikeCount = findViewById(R.id.tvLikeCount);

//        btnReply = findViewById(R.id.btnReply);
        ibLikeEmpty = findViewById(R.id.ibLikeEmpty);
        ibLike = findViewById(R.id.ibLike);
        ibLike.setVisibility(View.GONE);

//        btnReply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        ibLikeEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.likeTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("DEBUG", "Liked tweet");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("DEBUG", "Like tweet error: " + throwable.toString());
                    }
                }, tweet.id);
                ibLikeEmpty.setVisibility(View.GONE);
                ibLike.setVisibility(View.VISIBLE);
            }
        });
        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.unlikeTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("DEBUG", "Unliked tweet");
                    }
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("DEBUG", "Unlike tweet error: " + throwable.toString());
                    }
                }, tweet.id);
                ibLike.setVisibility(View.GONE);
                ibLikeEmpty.setVisibility(View.VISIBLE);
            }
        });

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        tvName.setText("@" + tweet.user.screenName);
        tvScreenName.setText(tweet.user.name);
        tvBody.setText(tweet.body);
        tvTimeStamp.setText(tweet.createdAt);

        tvRetweetCount.setText(tweet.retweetCount + " Retweets");
        tvLikeCount.setText(tweet.likeCount + " Likes");

        if(tweet.imageUrl != null) {
            Glide.with(this).load(tweet.imageUrl).into(ivTweetImage);
        }
        else {
            ivTweetImage.setVisibility(View.GONE);
        }
        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);
    }
}