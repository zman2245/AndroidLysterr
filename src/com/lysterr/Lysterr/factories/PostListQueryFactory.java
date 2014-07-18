package com.lysterr.Lysterr.factories;

import android.os.Handler;
import android.os.Looper;
import com.lysterr.Lysterr.data.ParseClass;
import com.lysterr.Lysterr.data.ParsePostField;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class PostListQueryFactory implements ParseQueryAdapter.QueryFactory<ParseObject> {

    private ParseQuery<ParseObject> mQuery;

    private ParseQuery<ParseObject> sAllQuery;
    private ParseQuery<ParseObject> sLocationQuery;
    private ParseQuery<ParseObject> sMyQuery;

    public PostListQueryFactory() {
        setAllQuery();
    }

    @Override
    public ParseQuery<ParseObject> create() {
        return mQuery;
    }

    public void setMyQuery() {
        sMyQuery = new ParseQuery<ParseObject>(ParseClass.Post.toString());
        sMyQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        sMyQuery.orderByDescending("updatedAt");
        sMyQuery.include(ParsePostField.createdBy.toString());
        sMyQuery.whereEqualTo(ParsePostField.createdBy.toString(), ParseUser.getCurrentUser());

        mQuery = sMyQuery;
    }

    public void setLocationQuery(double latitude, double longitude, double distance) {
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);

        sLocationQuery = new ParseQuery<ParseObject>(ParseClass.Post.toString());
        sLocationQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        sLocationQuery.orderByDescending("updatedAt");
        sLocationQuery.include(ParsePostField.createdBy.toString());
        sLocationQuery.whereWithinMiles(ParsePostField.location.toString(), point, distance);

        mQuery = sLocationQuery;
    }

    public void setAllQuery() {
        sAllQuery = new ParseQuery<ParseObject>(ParseClass.Post.toString());
        sAllQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        sAllQuery.orderByDescending("updatedAt");
        sAllQuery.include(ParsePostField.createdBy.toString());

        mQuery = sAllQuery;
    }

    public void setSearchText(final String searchText, final Runnable callback) {
        // mQuery.cancel() can block
        new Thread(new Runnable() {
            @Override
            public void run() {
                mQuery.cancel();
                mQuery.whereMatches(ParsePostField.text.toString(), "\\b" + searchText, "i");

                new Handler(Looper.getMainLooper()).post(callback);
            }
        }).start();
    }
}
