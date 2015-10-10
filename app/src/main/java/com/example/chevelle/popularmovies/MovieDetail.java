package com.example.chevelle.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.json.JSONTokener;

public class MovieDetail extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        String key = getString(R.string.movieDetailKey);
        String detail = (String)intent.getCharSequenceExtra(key);

        populateView(detail);
    }

    private void populateView(String detail) {
        TextView movieText = null;
        ImageView movieView = null;

        try {
            String content = null;
            JSONObject info = new JSONObject(new JSONTokener(detail));
            String imageUrl = getImageUrl(info);

            movieText = (TextView)findViewById(R.id.movieTitle);
            movieText.setText(info.getString("original_title"));

            movieText = (TextView)findViewById(R.id.rating);
            content = "Rating: " + info.getString("vote_average");
            movieText.setText(content);

            movieText = (TextView)findViewById(R.id.releaseDate);
            content = "Release Date: " + info.getString("release_date");
            movieText.setText(content);

            movieText = (TextView)findViewById(R.id.overview);
            content = "Overview: \n\n" + info.getString("overview");
            movieText.setText(content);

            movieView = (ImageView)findViewById(R.id.movieImg);
            Picasso.with(this).load(imageUrl).into(movieView);

        }
        catch (Exception anyErr) { }
    }

    private String getImageUrl(JSONObject info) {
        StringBuffer imageUrl = new StringBuffer();
        String baseUrl = getString(R.string.baseImageUrl);

        try {
            imageUrl.append(baseUrl);
            imageUrl.append("w185");
            imageUrl.append(info.getString("poster_path"));
        }
        catch (Exception anyError) { }


        return imageUrl.toString();
    }
}
