package com.michaelbraha.popular_movies.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 3/4/2016.
 */
public class MovieItem implements Parcelable {

    private String mImageUrl;
    private String mTitle;
    private String mOverview;
    private String mVote;
    private String mReleaseDate;
    private String mMovieId;

    public MovieItem() {
        super();
    }

    public MovieItem(String fullPath){
        this.mImageUrl = extractImageUrl(fullPath);
        this.mTitle = extractMovieTitle(fullPath);
        this.mOverview = extractMovieOverview(fullPath);
        this.mVote = extractMoveVotes(fullPath);
        this.mReleaseDate = extractReleaseData(fullPath);
        this.mMovieId = extractMovieId(fullPath);

    }

    public void setEntireMovieItem(String image, String title, String overview, String vote,
                     String date, String movieId) {
        this.mImageUrl = image;
        this.mTitle = title;
        this.mOverview = overview;
        this.mVote = vote;
        this.mReleaseDate = date;
        this.mMovieId = movieId;
    }

    private MovieItem(Parcel in){
        mImageUrl = in.readString();
        mTitle = in.readString();
        mOverview = in.readString();
        mVote = in.readString();
        mReleaseDate = in.readString();
        mMovieId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mImageUrl);
        dest.writeString(mTitle);
        dest.writeString(mOverview);
        dest.writeString(mVote);
        dest.writeString(mReleaseDate);
        dest.writeString(mMovieId);
    }

    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {

        @Override
        public MovieItem createFromParcel(Parcel parcel){
            return new MovieItem(parcel);
        }

        @Override
        public MovieItem[] newArray(int i){
            return new MovieItem[i];
        }
    };

    public String getImage() {
        return mImageUrl;
    }

    public void setImage(String img) {
        this.mImageUrl = img;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getVote() {
        return mVote;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getMovieId(){
        return mMovieId;
    }


    public String extractImageUrl(String result){
        String url;
        String[] parts = extractDataIntoParts(result);
        url = parts[0];
        return url;
    }

    public String extractMovieTitle(String result) {
        String title;
        String[] parts = extractDataIntoParts(result);
        title = parts[1];
        return title;
    }

    public String extractMovieOverview(String result){
        String overview;
        String[] parts = extractDataIntoParts(result);
        overview = parts[2];
        return overview;
    }

    public String extractReleaseData(String result){
        String releaseDate;
        String[] parts = extractDataIntoParts(result);
        releaseDate = parts[3];
        return releaseDate;
    }

    public String extractMoveVotes(String result){
        String votes;
        String[] parts = extractDataIntoParts(result);
        votes = parts[4];
        return votes;
    }

    public String extractMovieId(String result){
        String id;
        String[] parts = extractDataIntoParts(result);
        id = parts[5];
        return id;
    }


    public String[] extractDataIntoParts(String result){
        return result.split("\\*");
    }
}
