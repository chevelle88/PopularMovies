package com.example.chevelle.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;

import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private String sharedPrefName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefName = getString(R.string.preferencesName);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesFragment())
                    .commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();
        String selectedOption = null;

        switch(id) {
            case R.id.action_sort_popular:
                selectedOption = getString(R.string.sort_popular);
                break;

            case R.id.action_sort_rate:
                selectedOption = getString(R.string.sort_rate);
                break;

            case R.id.action_list_favs:
                selectedOption = getString(R.string.sort_favs);
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
        String defaultSort = getString(R.string.sort_popular);

        if (favItem != null) {
            favItem.setEnabled((favs.size() > 0));
        }

        refreshMovieListing(defaultSort);

        return super.onPrepareOptionsMenu(menu);
    }

    private void refreshMovieListing(String selectedOption) {
        Fragment movies = getFragmentManager().findFragmentById(R.id.container);
        ((MoviesFragment)movies).loadMovieListing(selectedOption);
    }
}
