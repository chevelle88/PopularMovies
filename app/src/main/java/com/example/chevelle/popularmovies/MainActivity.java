package com.example.chevelle.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;

import java.util.Map;


public class MainActivity extends ActionBarActivity
        implements MoviesFragment.OnMovieSelectedListener {

    private String sharedPrefName;
    private boolean multiPaned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefName = getString(R.string.preferencesName);

        setMultiPanedState();

        if (savedInstanceState == null) {

            if (!isMultiPaned()) {
                getFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesFragment())
                    .commit();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update menu.
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String selectedOption = null;
        int selectedOptionId = item.getItemId();

        switch(selectedOptionId) {

            case R.id.action_sort_rate:
                selectedOption = getString(R.string.sort_rate);
                break;

            case R.id.action_list_favs:
                selectedOption = getString(R.string.sort_favs);
                break;

            default:
                selectedOption = getString(R.string.sort_popular);
                break;

        }

        // Update the movie listing with a new sort order.
        refreshMovieListing(selectedOption);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem favItem = menu.findItem(R.id.action_list_favs);
        SharedPreferences favsDb = getSharedPreferences(sharedPrefName, MODE_PRIVATE);
        Map<String, String> favs = (Map<String, String>)favsDb.getAll();

        if (favItem != null) {
            favItem.setEnabled((favs.size() > 0));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onMovieSelected(String movieDetail) {
        Fragment movieInfo = getFragmentManager().findFragmentById(R.id.movieInfo);

        ((MovieDetailFragment)movieInfo).loadMovieInfo(movieDetail);
    }

    public boolean isMultiPaned() {
        return multiPaned;
    }

    private void refreshMovieListing(String selectedOption) {
        int fragId = (multiPaned) ? R.id.movieLibrary : R.id.container;
        Fragment movies = getFragmentManager().findFragmentById(fragId);

        ((MoviesFragment)movies).loadMovieListing(selectedOption);
    }

    private void setMultiPanedState() {
        Fragment movieInfo = getFragmentManager().findFragmentById(R.id.movieInfo);
        Fragment movieList = getFragmentManager().findFragmentById(R.id.movieLibrary);


        multiPaned = ((movieList != null) && (movieInfo != null));
    }

}
