package com.example.chevelle.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

import java.util.List;

/**
 * Created by chevelle on 12/13/15.
 */
public class MovieReviewAdapter extends BaseAdapter {

    private List<String> movieReviews;
    private Context context;

    public MovieReviewAdapter(Context context) {
        this.context = context;
    }

    public int getCount() {
        return movieReviews.size();
    }

    public String getItem(int position) {

        return movieReviews.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView review = (TextView) convertView;

        if (review == null) {
            review = new TextView(context);
            review.setVerticalScrollBarEnabled(true);
            review.setMovementMethod(ScrollingMovementMethod.getInstance());
        }

        review.setText(movieReviews.get(position));

        return review;
    }

    public void setReviews(List<String> movieReviews) {
        this.movieReviews = movieReviews;
    }
}
