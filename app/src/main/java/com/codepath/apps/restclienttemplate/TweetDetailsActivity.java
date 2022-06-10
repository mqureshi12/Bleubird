package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.Objects;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
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
    ImageButton ibLikeEmpty;
    ImageButton ibLike;
    ImageButton ibRetweetEmpty;
    ImageButton ibRetweet;

    private ActivityTweetDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTweetDetailBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_tweet_detail);
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.twitter_blue)));
        Objects.requireNonNull(getSupportActionBar()).setTitle("Details");
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.twitter_dark));

        ivProfileImage = binding.ivProfileImage;
        ivTweetImage = binding.ivMedia;
        tvScreenName = binding.tvScreenName;
        tvName = binding.tvName;
        tvBody = binding.tvBody;
        tvTimeStamp = binding.tvTimeStamp;

        tvRetweetCount = binding.tvRetweetCount;
        tvLikeCount = binding.tvLikeCount;

        ibLikeEmpty = binding.ibLikeEmpty;
        ibLike = binding.ibLike;
        ibLike.setVisibility(View.GONE);
        ibRetweetEmpty = binding.ibRetweetEmpty;
        ibRetweet = binding.ibRetweet;

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        if(tweet.favorited) {
            ibLike.setVisibility(View.VISIBLE);
            ibLikeEmpty.setVisibility(View.GONE);
        }
        else {
            ibLike.setVisibility(View.GONE);
            ibLikeEmpty.setVisibility(View.VISIBLE);
        }

        if(tweet.retweeted) {
            ibRetweet.setVisibility(View.VISIBLE);
            ibRetweetEmpty.setVisibility(View.GONE);
        }
        else {
            ibRetweet.setVisibility(View.GONE);
            ibRetweetEmpty.setVisibility(View.VISIBLE);
        }

        ibRetweetEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.retweetTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("DEBUG", "Retweeted tweet");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("DEBUG", "Retweet error: " + throwable.toString());
                    }
                }, tweet.id);
                ibLikeEmpty.setVisibility(View.GONE);
                ibLike.setVisibility(View.VISIBLE);
            }
        });

        ibRetweetEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.retweetTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("DEBUG", "Retweeted tweet");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("DEBUG", "Retweet tweet error: " + throwable.toString());
                    }
                }, tweet.id);
                ibRetweetEmpty.setVisibility(View.GONE);
                ibRetweet.setVisibility(View.VISIBLE);

                tweet.retweetCount++;
                tweet.retweeted = true;
                tvRetweetCount.setText(tweet.retweetCount + " Retweets");

            }
        });
        ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.unretweetTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("DEBUG", "Unretweeted tweet");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("DEBUG", "Unretweet tweet error: " + throwable.toString());
                    }
                }, tweet.id);
                ibRetweet.setVisibility(View.GONE);
                ibRetweetEmpty.setVisibility(View.VISIBLE);

                tweet.retweetCount--;
                tweet.retweeted = false;
                tvRetweetCount.setText(tweet.retweetCount + " Retweets");
            }
        });

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
        Glide.with(this).load(tweet.user.profileImageUrl).transform(new RoundedCornersTransformation(100, 1)).into(ivProfileImage);
    }
}