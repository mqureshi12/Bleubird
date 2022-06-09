package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utilities.DateUtility;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private static final String TAG = "TweetsAdapter";
    public static final int MAX_TWEET_LENGTH = 280;

    Context context;
    List<Tweet> tweets;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate a the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements android.view.View.OnClickListener {

        ImageView ivProfileImage;
        ImageView ivTweetImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvTimeStamp;
        Tweet tweet;
        EditText etReply;
        Button btnReply;
        ImageButton ibSend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTweetImage = itemView.findViewById(R.id.ivTweetImage);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimeStamp = itemView.findViewById(R.id.timestamp);

            etReply = itemView.findViewById(R.id.etReply);
            btnReply = itemView.findViewById(R.id.btnReply);
            ibSend = itemView.findViewById(R.id.ibSend);

            btnReply.setOnClickListener(this);
            ibSend.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            this.tweet = tweet;
            setDefaultConditions();

            tvTimeStamp.setText(tweet.timeStamp);
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);

            etReply.setText("@" + tweet.user.screenName + " ");
            etReply.setSelection(etReply.getText().length());

            Glide.with(context).load(tweet.user.profileImageUrl).transform(new RoundedCornersTransformation(100, 1)).into(ivProfileImage);
            if(tweet.imageUrl != null) {
                Glide.with(context).load(tweet.imageUrl).centerCrop().transform(new RoundedCornersTransformation(30, 10)).override(Target.SIZE_ORIGINAL).into(ivTweetImage);
            } else {
                ivTweetImage.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            if (view == btnReply) {
                etReply.setVisibility(View.VISIBLE);
                ibSend.setVisibility(View.VISIBLE);
                btnReply.setVisibility(View.GONE);
            } else if (view == ibSend) {
                String replyContent = etReply.getText().toString();
                if (replyContent.isEmpty()) {
                    Toast.makeText(context, "Sorry, your reply cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (replyContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(context, "Sorry, your reply is too long", Toast.LENGTH_LONG).show();
                    return;
                }

                TwitterClient client = TwitterApp.getRestClient(context);

                // Make an API call to Twitter to publish the reply
                client.publishReply(replyContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess reply");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure reply", throwable);
                    }
                }, tweet.id);
                hideKeyboard(view);
                setDefaultConditions();
            } else {
                hideKeyboard(view);
                setDefaultConditions();

                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Tweet tweet = tweets.get(position);
                    Intent intent = new Intent(context, TweetDetailsActivity.class);
                    // serialize the movie using parceler, use its short name as a key
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    // show the activity
                    context.startActivity(intent);
                }
            }
        }

        public void setDefaultConditions() {
            etReply.setText("@" + tweet.user.screenName + " ");
            etReply.setSelection(etReply.getText().length());
            etReply.setVisibility(View.GONE);
            ibSend.setVisibility(View.GONE);
            btnReply.setVisibility(View.VISIBLE);
            btnReply.setBackgroundResource(R.drawable.ic_vector_compose_dm_fab);
        }

        public void hideKeyboard(View view) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
