package com.michaelbraha.popular_movies;

import java.util.ArrayList;

public class Trailer {
    private String mIndividualURL;
    private String mTrailerName;
    private String[] mTrailers;

    public Trailer(){

    }
    public Trailer(String name) {
        this.mTrailerName = name;
    }

    public String getName() {
        return this.mTrailerName;
    }

    public boolean hasTrailers() {
        if (this.mTrailers != null) {
            return true;
        }
        return false;
    }

    public String[] getTrailers() {
        return this.mTrailers;
    }

    public void setIndividualURL(String individualURL) {
        this.mIndividualURL = individualURL;
    }

    public String getIndividualURL() {
        return this.mIndividualURL;
    }

    public void setURLs(String[] trailers) {
        this.mTrailers = trailers;
    }

    public String[] getURLs() {
        return this.mTrailers;
    }

    public String getSpecificURL(int position) {
        if (this.mTrailers == null || position > this.mTrailers.length) {
            return null;
        }
        return this.mTrailers[position];
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