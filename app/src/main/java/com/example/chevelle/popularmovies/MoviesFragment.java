package com.example.chevelle.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.GridView;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Collection;


public class MoviesFragment extends Fragment {

    private static String SAVE_SORT_OPTION;

    private String prefName;
    private String favOption;
    private String sortOption;
    private boolean multiPanedActivity = false;
    private OnMovieSelectedListener movieListener;

    public interface OnMovieSelectedListener {
        public void onMovieSelected(String movieDetail);
    }

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(SAVE_SORT_OPTION, sortOption);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            multiPanedActivity = ((MainActivity)activity).isMultiPaned();
            movieListener = (OnMovieSelectedListener)activity;
        }
        catch (ClassCastException castErr) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnMovieSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize.
        prefName = getString(R.string.preferencesName);
        favOption =  getString(R.string.sort_favs);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_movies, container, false);

        // Inflate the layout for this fragment
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String initialSort = null;

        if (savedInstanceState == null) {
            initialSort = getString(R.string.sort_popular);
        }
        else {
            initialSort = savedInstanceState.getString(SAVE_SORT_OPTION);
        }

        // Get popular movies.
        loadMovieListing(initialSort);
    }

    public void loadMovieListing(String selectedOption) {

        // Set the current sort selection.
        sortOption = selectedOption;

        // Load movie collection based upon selected menu option.
        if (selectedOption.equalsIgnoreCase(favOption)) {
            loadFromSharedPreferences();
        }
        else {
            loadFromMovieDb(selectedOption);
        }
    }

    public void setMovieResults(JSONArray results) {
        final MoviesAdapter movieAdapter = new MoviesAdapter(getActivity());
        GridView gridView = (GridView) getActivity().findViewById(R.id.movieList);

        // Load the movie results into the adapter.
        movieAdapter.setItems(results);

        // Set the adapter into the view.
        gridView.setAdapter(movieAdapter);

        // Set a onItemClickListener on the grid.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Adapter movieAdapter = parent.getAdapter();
                JSONObject info = ((MoviesAdapter)movieAdapter).getItem(position);
                String movieInfo = info.toString();

                if (multiPanedActivity) {
                    movieListener.onMovieSelected(movieInfo);
                }
                else {
                    showMovieDetail(movieInfo);
                }
            }

        });
    }

    private void showMovieDetail(String movieInfo) {
        String key = getString(R.string.movieDetailKey);
        Intent movieIntent = new Intent(getActivity(), MovieDetail.class);

        movieIntent.putExtra(key, movieInfo);
        getActivity().startActivity(movieIntent);
    }

    private void loadFromSharedPreferences() {

        try {
            String favs = prefsToJson();
            setMovieResults(new JSONArray(favs));
        }
        catch (Exception err) {
            System.out.println("loadFromSharedPreferences (Error): " + err.getMessage());
        }

    }

    private void loadFromMovieDb(String sortBy) {
        String movieUrl = getString(R.string.movieDbUrl);
        String apiKey = getString(R.string.apiKey);

        new MoviesAsyncTask().execute(movieUrl, sortBy, apiKey);
    }

    private String prefsToJson() {
        int count = 0;
        SharedPreferences favsDb =
                getActivity().getSharedPreferences(prefName, getActivity().MODE_PRIVATE);
        Collection<String> favValues = (Collection<String>)favsDb.getAll().values();
        StringBuilder favs = new StringBuilder("[");

        for (String value : favValues) {

            if (count > 0) {
                favs.append(",");
            }

            favs.append(value);
            ++count;
        }

        favs.append("]");

        return favs.toString();
    }

    class MoviesAsyncTask extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String[] urlParts) {
            String line = null;
            JSONArray results = null;
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

                JSONObject movieObj = new JSONObject(new JSONTokener(data.toString()));
                results = movieObj.getJSONArray("results");

            }
            catch (Exception anyError) {
                System.out.println("Exception: " + anyError.getMessage());
            }
            finally {
                MovieIOUtil.closeIO(reader);
                site.disconnect();
            }

            return results;
        }

        protected void onPostExecute(JSONArray results) {
            setMovieResults(results);
        }

        private String buildMovieUrl(String [] urlParts) {
            StringBuilder movieUrl = new StringBuilder(urlParts[0] + "?");

            // Append the url parameters
            movieUrl.append("sort_by=" + urlParts[1]);
            movieUrl.append("&");
            movieUrl.append("api_key=" + urlParts[2]);

            return movieUrl.toString();
        }

    }

    // Create a public interface to communicate with the activity to send a message to the other fragment
}
