package com.example.chevelle.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by chevelle on 10/3/15.
 */
public class MoviesAdapter extends BaseAdapter {

    private JSONArray items;
    private Context context;

    public MoviesAdapter(Context context) {
        this.context = context;
    }

    public int getCount() {
        int count = 0;

        if (items != null) {
            count = items.length();
        }

        return count;
    }

    public JSONObject getItem(int position) {
        JSONObject item = null;

        try {
            item = items.getJSONObject(position);
        }
        catch (Exception anyError) { }

        return item;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        String posterUrl = null;
        ImageView poster = (ImageView) convertView;

        try {
            if (poster == null) {
                poster = new ImageView(context);
            }

            posterUrl = getImageUrl(items.getJSONObject(position));

            Picasso.with(context).load(posterUrl).into(poster);
        }
        catch (Exception anyError) {
            anyError.printStackTrace();
        }

        return poster;
    }

    public void setItems(JSONArray results) {
        items = results;
    }

    public JSONArray getItems() {
        return items;
    }

    private String getImageUrl(JSONObject movieInfo) {
        StringBuffer imageUrl = new StringBuffer();
        String baseUrl = context.getString(R.string.baseImageUrl);

        try {
            imageUrl.append(baseUrl);
            imageUrl.append("w185");
            imageUrl.append(movieInfo.getString("poster_path"));
        }
        catch (Exception anyError) { }


        return imageUrl.toString();
    }
}
