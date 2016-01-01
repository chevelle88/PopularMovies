package com.example.chevelle.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class MovieDetail extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            MovieDetailFragment detailFrag = new MovieDetailFragment();

            args.putString(MovieDetailFragment.ARG_DETAIL, getDetailInfo());
            detailFrag.setArguments(args);

            getFragmentManager().beginTransaction()
                    .add(R.id.detailContainer, detailFrag)
                    .commit();
        }

    }

    public void showTrailer(View view) {
        MovieDetailFragment detailFrag = (MovieDetailFragment) getFragmentManager()
                .findFragmentById(R.id.detailContainer);

        detailFrag.showTrailer(view);
    }

    private String getDetailInfo() {
        Intent intent = getIntent();
        String key = getString(R.string.movieDetailKey);
        String detail = (String) intent.getCharSequenceExtra(key);

        return detail;
    }

}
