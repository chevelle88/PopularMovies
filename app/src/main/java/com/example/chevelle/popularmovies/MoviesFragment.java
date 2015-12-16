package com.example.chevelle.popularmovies;

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

    private String prefName;
    private String favOption;

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_movies, container, false);
        String initialSort = getString(R.string.sort_popular);

        // Get favorites strings.
        prefName = getString(R.string.preferencesName);
        favOption =  getString(R.string.sort_favs);

        // Get popular movies.
        loadMovieListing(initialSort);

        // Inflate the layout for this fragment
        return fragmentView;
    }

    public void loadMovieListing(String selectedOption) {

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
                String key = getString(R.string.movieDetailKey);
                Intent movieIntent = new Intent(getActivity(), MovieDetail.class);
                JSONObject info = ((MoviesAdapter) movieAdapter).getItem(position);

                movieIntent.putExtra(key, info.toString());
                getActivity().startActivity(movieIntent);
            }

        });
    }

    private void loadFromSharedPreferences() {

        try {
            String favs = prefsToJson();
            setMovieResults(new JSONArray(favs));
        }
        catch (Exception err) { }

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

}
