package com.example.chevelle.popularmovies;

import java.io.Closeable;

/**
 * Created by chevelle on 12/13/15.
 */
public class MovieIOUtil {

    public static void closeIO(Closeable resource) {
        try {
            if (resource != null) {
                resource.close();
            }
        }
        catch (Exception anyError) { }
    }
}
