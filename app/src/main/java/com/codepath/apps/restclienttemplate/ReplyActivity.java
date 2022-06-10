package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityReplyBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.Objects;

import okhttp3.Headers;

public class ReplyActivity extends AppCompatActivity {

    public static final String TAG = "ReplyActivity";
    public static final int MAX_TWEET_LENGTH = 280;
    private ActivityReplyBinding binding;

    TextView tvOriginalAuthor;
    TextView tvCharacterCounter;
    EditText etReply;
    Button btnTweet;
    String originalAuthor;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.twitter_blue)));
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        binding = ActivityReplyBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_reply);
        setContentView(binding.getRoot());

        client = TwitterApp.getRestClient(this);

        tvOriginalAuthor = binding.tvOriginalAuthor;
        tvCharacterCounter = binding.tvCharacterCounter;
        etReply = binding.etReply;
        btnTweet = binding.btnTweet;

        Bundle extras = getIntent().getExtras();
        originalAuthor = extras.getString("original_author");

        tvOriginalAuthor.setText("Replying to @" + originalAuthor);
        etReply.setText("@" + originalAuthor + " ");
        etReply.setSelection(etReply.length());

        TextWatcher twCompose = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharacterCounter.setText(String.valueOf(s.length()) + "/280");
            }

            public void afterTextChanged(Editable s) {
            }
        };
        etReply.addTextChangedListener(twCompose);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etReply.getText().toString();
                if(tweetContent.isEmpty()) {
                    Toast.makeText(ReplyActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ReplyActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }

                // Make an API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
            }
        });
    }
}