package com.example.chevelle.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesFragment())
                    .commit();
        }
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
        }

        // Update the movie listing with a new sort order.
        Fragment movies = getFragmentManager().findFragmentById(R.id.container);
        ((MoviesFragment)movies).loadMovieListing(selectedOption);

        return super.onOptionsItemSelected(item);
    }
}
