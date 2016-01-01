package com.example.chevelle.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chevelle on 12/27/15.
 */
public class MovieDetailFragment extends Fragment {

    private boolean favMovie;
    private String movieId = null;
    private JSONObject info = null;
    private JSONObject infoExtras = null;
    private SharedPreferences favsDb = null;
    private Map<String, String> movieTrailers;

    public static String ARG_DETAIL = "detailArg";
    private static String SAVE_SELECTED_MOVIE = "saveSelectedMovie";
    private static String SAVE_REVIEWS_TRAILERS = "saveReviewsTrailers";

    public MovieDetailFragment() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String prefName = getString(R.string.preferencesName);
        favsDb = getActivity().getSharedPreferences(prefName, getActivity().MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_movie_detail,
                container, false);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle args = null;

        super.onActivityCreated(savedInstanceState);
        args = getArguments();

        if (savedInstanceState == null) {

            if (args != null) {
                loadMovieInfo(args.getString(ARG_DETAIL));
            }
        }
        else {
            restoreMovieInfo(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if ((info != null) && (infoExtras != null)) {
            outState.putString(SAVE_SELECTED_MOVIE, info.toString());
            outState.putSerializable(SAVE_REVIEWS_TRAILERS, infoExtras.toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = favsDb.edit();

        if (favMovie) {
            if (!favsDb.contains(movieId)) {
                editor.putString(movieId, info.toString());
                editor.commit();
            }
        }
        else {
            if (favsDb.contains(movieId)) {
                editor.remove(movieId);
                editor.commit();
            }
        }
    }

    public void loadMovieInfo(String detail) {

        try {

            info = new JSONObject(new JSONTokener(detail));
            movieId = info.getString("id");

            // Get extra information about the movie.
            String movieUrl = getString(R.string.movieUrl);
            String apiKey = getString(R.string.apiKey);
            new DetailAsyncTask().execute(movieUrl, movieId, apiKey);

        }
        catch (Exception err) { }
    }

    private void populateView() {

        try {
            setMovieInfo();
            setMovieTrailers(infoExtras.getJSONObject("videos"));
            setMovieReviews(infoExtras.getJSONObject("reviews"));
            configureFavorite();
        }
        catch (Exception anyErr) {
        }
    }

    public void showTrailer(View view) {
        String title = (String)((TextView)view).getText();
        String url = buildTrailerUrl(title);
        Uri video = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, video);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    private void restoreMovieInfo(Bundle savedMovieState) {

        try {

            info = new JSONObject(
                    new JSONTokener(savedMovieState.getString(SAVE_SELECTED_MOVIE)));
            infoExtras = new JSONObject(
                    new JSONTokener(savedMovieState.getString(SAVE_REVIEWS_TRAILERS)));
            movieId = info.getString("id");

            setMovieInfo();
            populateView();

            System.out.println("restoring movie state");
        }
        catch (Exception err) { }


    }

    private void setMovieInfo() throws JSONException {
        String content = null;
        TextView movieText = null;
        ImageView movieView = null;

        Activity activity = getActivity();
        String imageUrl = getImageUrl(info);

        movieText = (TextView) activity.findViewById(R.id.movieTitle);
        movieText.setText(info.getString("original_title"));

        movieText = (TextView) activity.findViewById(R.id.rating);
        content = "Rating: " + info.getString("vote_average");
        movieText.setText(content);

        movieText = (TextView) activity.findViewById(R.id.releaseDate);
        content = "Release Date: " + info.getString("release_date");
        movieText.setText(content);

        movieText = (TextView) activity.findViewById(R.id.overview);
        content = info.getString("overview");
        movieText.setText(content);

        movieView = (ImageView) activity.findViewById(R.id.movieImg);
        Picasso.with(activity).load(imageUrl).into(movieView);

    }

    private String getImageUrl(JSONObject info) throws JSONException {
        StringBuffer imageUrl = new StringBuffer();
        String baseUrl = getString(R.string.baseImageUrl);

        imageUrl.append(baseUrl);
        imageUrl.append("w185");
        imageUrl.append(info.getString("poster_path"));

        return imageUrl.toString();
    }

    private void setMovieTrailers(JSONObject trailers) throws JSONException {
        String title = null;
        String trailerId = null;
        JSONObject trailer = null;
        List<String> titles = new ArrayList<String>();
        JSONArray results = trailers.getJSONArray("results");

        movieTrailers = new HashMap<String, String>();

        for (int idx = 0; idx < results.length(); idx++) {
            trailer = results.getJSONObject(idx);
            title = trailer.getString("name");
            trailerId = trailer.getString("key");

            titles.add(title);
            movieTrailers.put(trailerId, title);
        }

        ArrayAdapter<String> trailerList =
                new ArrayAdapter<String>(getActivity(), R.layout.trailer_list_item, titles);
        ListView trailerView = (ListView) getActivity().findViewById(R.id.trailerList);
        trailerView.setAdapter(trailerList);
    }

    private void setMovieReviews(JSONObject reviews) throws JSONException {
        JSONObject review = null;
        ListView reviewView = null;
        List<String> movieReviews = new ArrayList<String>();
        MovieReviewAdapter reviewList = new MovieReviewAdapter(getActivity());

        JSONArray results = reviews.getJSONArray("results");

        for (int idx = 0; idx < results.length(); idx++) {
            review = results.getJSONObject(idx);
            movieReviews.add(review.getString("content"));
        }

        reviewView = (ListView) getActivity().findViewById(R.id.reviewList);
        reviewList.setReviews(movieReviews);
        reviewView.setAdapter(reviewList);
    }

    private void configureFavorite() {
        ToggleButton favBtn = (ToggleButton) getActivity().findViewById(R.id.favState);

        favMovie = favsDb.contains(movieId);
        favBtn.setChecked(favMovie);

        favBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                favMovie = isChecked;
            }
        });
    }

    private String buildTrailerUrl(String title) {
        String trailerTitle = null;
        StringBuilder url = new StringBuilder();
        Set<String> trailerIds = movieTrailers.keySet();

        for (String trailerId : trailerIds) {
            trailerTitle = movieTrailers.get(trailerId);

            if (title.equalsIgnoreCase(trailerTitle)) {
                url.append(getString(R.string.videoUrl));
                url.append(trailerId + "?autoplay=1");
                break;
            }
        }

        return url.toString();
    }

    class DetailAsyncTask extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String [] urlParts) {
            String line = null;
            JSONObject movieObj = null;
            HttpURLConnection site = null;
            BufferedReader reader = null;
            StringBuilder data = new StringBuilder();

            try {

                URL movieUrl = new URL(buildMovieUrl(urlParts));
                site = (HttpURLConnection)movieUrl.openConnection();

                reader = new BufferedReader(new InputStreamReader(site.getInputStream()));

                while ((line = reader.readLine()) != null) {
                    data.append(line);
                }

                movieObj = new JSONObject(new JSONTokener(data.toString()));

            }
            catch (Exception anyError) {
                System.out.println("Exception: " + anyError.getMessage());
            }
            finally {
                MovieIOUtil.closeIO(reader);
                site.disconnect();
            }

            return movieObj;
        }

        protected void onPostExecute(JSONObject result) {
            infoExtras = result;
            populateView();
        }

        private String buildMovieUrl(String [] urlParts) {
            StringBuilder movieUrl = new StringBuilder(urlParts[0]);

            // Append movie path.
            movieUrl.append("/" + urlParts[1] + "?");

            // Append url parameters.
            movieUrl.append("append_to_response=reviews,videos&");
            movieUrl.append("api_key=" + urlParts[2]);

            return movieUrl.toString();
        }

    }
}
