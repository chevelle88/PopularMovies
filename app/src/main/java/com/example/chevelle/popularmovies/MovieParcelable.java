package com.example.chevelle.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chevelle on 1/3/16.
 */
public class MovieParcelable implements Parcelable {
    private String movie;

    public MovieParcelable(String movie) {
        this.movie = movie;
    }

    public String getMovie() {
        return movie;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movie);
    }

    public static final Parcelable.Creator<MovieParcelable> CREATOR =
            new Parcelable.Creator<MovieParcelable>() {

        @Override
        public MovieParcelable createFromParcel(Parcel source) {
            return new MovieParcelable(source);
        }

        public MovieParcelable[] newArray(int size) {
            return new MovieParcelable[size];
        }
    };

    private MovieParcelable(Parcel source) {
        movie = source.readString();
    }
}
