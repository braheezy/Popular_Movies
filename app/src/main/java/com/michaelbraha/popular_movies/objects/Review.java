package com.michaelbraha.popular_movies.objects;

import java.util.ArrayList;

/**
 * Created by Michael on 5/2/2016.
 */
public class Review {

    private ArrayList<String> mAuthorArray = new ArrayList<String>();
    private ArrayList<String> mReviewArray = new ArrayList<String>();
    private String mName;

    public Review (){
    }

    public Review(String name){
        mName = name;
    }

    public void setReviews (String[] result){
        for (String s : result){
            setAuthorArray(s);
            setReviewArray(s);
        }

    }

    public void setAuthorArray(String line){
        int starDelimiter = line.indexOf("*");
        String author = line.substring(0, starDelimiter);
        if (mAuthorArray != null){
            mAuthorArray.add(author);
        }
    }

    public void setReviewArray(String line){
        int starDelimiter = line.indexOf("*");
        String review = line.substring(starDelimiter + 1, line.length());
        if (mReviewArray != null){
            mReviewArray.add(review);
        }

    }

    public ArrayList<String> getAuthorArray(){
        return mAuthorArray;
    }

    public ArrayList<String> getReviewArray(){
        return mReviewArray;
    }

    public static ArrayList<Review> createReviewList(int numReviews) {
        ArrayList<Review> reviews = new ArrayList();
        int lastReviewId = 0;
        for (int i = 1; i <= numReviews; i++) {
            lastReviewId++;
            reviews.add(new Review("Review " + lastReviewId));
        }
        return reviews;
    }
}
