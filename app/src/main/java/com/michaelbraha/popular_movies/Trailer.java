package com.michaelbraha.popular_movies;

import java.util.ArrayList;

public class Trailer {
    private String mIndividualURL;
    private String mTrailerName;
    private String[] mTrailers;

    public Trailer(){
    }

    public Trailer(String name) {
        mTrailerName = name;
    }

    public String getName() {
        return mTrailerName;
    }

    public void setIndividualURL(String individualURL) {
        mIndividualURL = individualURL;
    }

    public String getIndividualURL() {
        return mIndividualURL;
    }

    public void setURLs(String[] trailers) {
        mTrailers = trailers;
    }

    public String getSpecificURL(int position) {
        if (mTrailers == null || position > mTrailers.length) {
            return null;
        }
        return mTrailers[position];
    }

    public static ArrayList<Trailer> createTrailerList(int numTrailers) {
        ArrayList<Trailer> trailers = new ArrayList();
        int lastTrailerId = 0;
        for (int i = 1; i <= numTrailers; i++) {
            lastTrailerId++;
            trailers.add(new Trailer("Trailer " + lastTrailerId));
        }
        return trailers;
    }
}