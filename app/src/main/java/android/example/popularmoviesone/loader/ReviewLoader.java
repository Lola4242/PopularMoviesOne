package android.example.popularmoviesone.loader;

import android.content.Context;
import android.example.popularmoviesone.model.Review;
import android.example.popularmoviesone.model.Trailer;
import android.example.popularmoviesone.utilities.MovieDbJsonUtils;
import android.example.popularmoviesone.utilities.NetworkUtils;
import android.support.v4.content.AsyncTaskLoader;

import java.net.URL;
import java.util.List;

import static android.example.popularmoviesone.utilities.MovieDbJsonUtils.parseTrailerListJson;

public class ReviewLoader extends AsyncTaskLoader<Review[]> {

    private String url;
    /* This String array will hold and help cache our movie data */
    Review[] mReviewrData = null;

    public ReviewLoader(Context context, String url){
        super(context);
        this.url=url;
    }

    /**
     * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
     */
    @Override
    protected void onStartLoading() {


        /*
         * If we already have cached results, just deliver them now. If we don't have any
         * cached results, force a load.
         */
        if (mReviewrData != null) {
            deliverResult(mReviewrData);
        } else {
            /*
             * When we initially begin loading in the background, we want to display the
             * loading indicator to the user
             */
            //mLoadingIndicator.setVisibility(View.VISIBLE);
            forceLoad();
        }
    }

    /**
     * This is the method of the AsyncTaskLoader that will load and parse the JSON data
     * from the Movie API in the background.
     *
     * @return Movie data from the API as an object.
     *         null if an error occurs
     */
    @Override
    public Review[] loadInBackground() {

            java.net.URL reviewRequestUrl = NetworkUtils.buidlUrl(url);

            try {
                String jsonReviewResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewRequestUrl);

                return MovieDbJsonUtils.parseReviewListJson(jsonReviewResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
    }

    /**
     * Sends the result of the load to the registered listener.
     *
     * @param data The result of the load
     */
    public void deliverResult(Review[] data) {
        mReviewrData = data;
        super.deliverResult(data);
    }
}
