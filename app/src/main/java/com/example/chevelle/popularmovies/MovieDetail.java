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
            JSONObject info = new JSONObject(new JSONTokener(detail));
            String imageUrl = getImageUrl(info);

            movieText = (TextView)findViewById(R.id.movieTitle);
            movieText.setText(info.getString("original_title"));

            movieText = (TextView)findViewById(R.id.rating);
            movieText.setText(info.getString("vote_average"));

            movieText = (TextView)findViewById(R.id.releaseDate);
            movieText.setText(info.getString("release_date"));

            movieText = (TextView)findViewById(R.id.overview);
            movieText.setText(info.getString("overview"));

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

    /* SAMPLE DATA
    {"adult":false,"backdrop_path":"/t5KONotASgVKq4N19RyhIthWOPG.jpg",
    "genre_ids":[28,12,878,53],"id":135397,
    "original_language":"en",
    "original_title":"Jurassic World",
    "overview":"Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.",
    "release_date":"2015-06-12",
    "poster_path":"/hTKME3PUzdS3ezqK5BZcytXLCUl.jpg",
    "popularity":66.494225,
    "title":"Jurassic World",
    "video":false,
    "vote_average":7.0,
    "vote_count":2464}
     */

}
