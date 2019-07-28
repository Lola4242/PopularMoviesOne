package android.example.popularmoviesone;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.example.popularmoviesone.loader.ReviewLoader;
import android.example.popularmoviesone.loader.TrailerLoader;
import android.example.popularmoviesone.model.Movie;
import android.example.popularmoviesone.model.Review;
import android.example.popularmoviesone.model.Trailer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks{

    private RecyclerView mRecyclerView;
    private ReviewAdapter mReviewAdapter;

    private Movie movie;

    private ImageView iMovieBackDrop;

    private TextView tMovieTitle;
    private TextView tMovieDetails;
    private TextView tMovieReleaseDate;
    private TextView tMovieVoteAverage;

    private GridView tTrailerListview;

    public static final String EXTRA_MOVIE= "extra_movie";

    private static final String URL = "https://image.tmdb.org/t/p/w500/";
    private static final String GIVEN_DATE = "yyyy-MM-dd";
    private static final String NICE_DATE = "dd MMM yyyy";
    private static final String RATING = " / 10";

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;
    private String TAG = "DetailActivity";

    private static final int REVIEW_LOADER_ID = 1;
    private static final String REVIEW_URL_EXTRA = "review";

    private static final int TRAILER_LOADER_ID = 2;
    private static final String TRAILER_URL_EXTRS = "trailer";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        iMovieBackDrop = findViewById(R.id.iv_back_drop);
        tMovieTitle = findViewById(R.id.tv_title);
        tMovieDetails = findViewById(R.id.tv_details);
        tMovieReleaseDate = findViewById(R.id.tv_release_date);
        tMovieVoteAverage = findViewById(R.id.tv_voteAverage);

        tTrailerListview = findViewById(R.id.trailers_gridview);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = findViewById(R.id.recyclerview_review);

        /* This TextView is used to display errors and will be hidden if there are no errors */

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);


        mRecyclerView.setHasFixedSize(true);

        /*
         * The ReviewAdapter is responsible for linking our review data with the Views that
         * will end up displaying our review data.
         */
        mReviewAdapter = new ReviewAdapter();
        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mReviewAdapter);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);



        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        if(!Objects.requireNonNull(intent).hasExtra(EXTRA_MOVIE)){
            closeOnError();
            return;

        }

        movie = Objects.requireNonNull(intent.getExtras()).getParcelable(EXTRA_MOVIE);

        if(movie == null){
            closeOnError();
            return;
        }


/*         * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
         * on our Loader at a later point in time through the support LoaderManager.
         */
        int loaderId = REVIEW_LOADER_ID;


/*         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * PopularMovieList. (implements LoaderCallbacks<PopularMovieList>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderManager.LoaderCallbacks<Review[]> callback = DetailActivity.this;


/*         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we add the URL via the bundle
         */
        Bundle reviewBundle = new Bundle();
        reviewBundle.putString(REVIEW_URL_EXTRA, "https://api.themoviedb.org/3/movie/" + movie.getId() + "/reviews");


/*         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(loaderId, reviewBundle, callback);

        /*
         * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
         * on our Loader at a later point in time through the support LoaderManager.
         */
        int loaderIdTrailer = TRAILER_LOADER_ID;

        /*
         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * PopularMovieList. (implements LoaderCallbacks<List<Trailer>>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderManager.LoaderCallbacks<List<Trailer>> callbackTrailer = DetailActivity.this;

        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback.
         */
        Bundle trailerBundle = new Bundle();
        trailerBundle.putString(TRAILER_URL_EXTRS, "https://api.themoviedb.org/3/movie/" + movie.getId() + "/videos");
        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(loaderIdTrailer, trailerBundle, callbackTrailer);

        populateUI();

    }

    private void populateUI() {
        Picasso.get().load(URL + movie.getBackdropPath()).into(iMovieBackDrop);
        tMovieTitle.setText(movie.getTitle());
        tMovieDetails.setText(movie.getOverview());

        Date releaseDate;
        String releaseDateText = "";
        try {
            releaseDate=new SimpleDateFormat(GIVEN_DATE).parse(movie.getReleaseDate());
            releaseDateText = new SimpleDateFormat(NICE_DATE, Locale.ENGLISH).format(releaseDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        tMovieReleaseDate.setText(releaseDateText);
        String voteAgarage = movie.getVoteAverage() + RATING;
        tMovieVoteAverage.setText(voteAgarage);

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    public void launchTrailer(Context context, Trailer trailer){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }


    private void showMovieData(){
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int i, @Nullable Bundle bundle) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        switch (i){
            case TRAILER_LOADER_ID:
                return new TrailerLoader(this,"https://api.themoviedb.org/3/movie/" + movie.getId() + "/videos");
            case REVIEW_LOADER_ID:
                return new ReviewLoader(this,"https://api.themoviedb.org/3/movie/" + movie.getId() + "/reviews");
        }


        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        switch (loader.getId()){
            case TRAILER_LOADER_ID:
                final List<Trailer> trailers = (List<Trailer>) data;
                TrailerAdapter trailerAdapter = new TrailerAdapter(this, trailers);
                tTrailerListview.setAdapter(trailerAdapter);
                tTrailerListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        launchTrailer(getApplicationContext(), trailers.get(position));
                    }
                });
                break;
            case REVIEW_LOADER_ID:
                Review[] reviews = (Review[]) data;
                if (reviews != null) {
                    showMovieData();
                    mReviewAdapter.setReviewData(reviews);
                } else {
                    showErrorMessage();
                }
        }


    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }



}
