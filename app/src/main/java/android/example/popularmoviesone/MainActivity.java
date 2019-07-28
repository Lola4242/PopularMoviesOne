package android.example.popularmoviesone;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.example.popularmoviesone.database.AppDatabase;
import android.example.popularmoviesone.database.MovieEntry;
import android.example.popularmoviesone.model.Movie;
import android.example.popularmoviesone.model.PopularMovieList;
import android.example.popularmoviesone.utilities.MapperUtils;
import android.example.popularmoviesone.utilities.MovieDbJsonUtils;
import android.example.popularmoviesone.utilities.NetworkUtils;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

import static android.example.popularmoviesone.DetailActivity.EXTRA_MOVIE;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, PopupMenu.OnMenuItemClickListener,
        LoaderManager.LoaderCallbacks<PopularMovieList> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclesView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;

    private static final String MOVIE_DB_URL_POPULAR = "https://api.themoviedb.org/3/movie/popular";
    private static final String MOVIE_DB_URL_TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated";

    private AppDatabase mDB;

    private static final int MOVIE_LOADER_ID = 0;
    private static final String MOVIE_URL_EXTRA = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclesView = findViewById(R.id.recyclerview_movies);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageTextView = findViewById(R.id.tv_error_message_display);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        mLoadingIndicator = findViewById(R.id.pb_loading_inidcator);

        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(this, 3);

        mRecyclesView.setLayoutManager(gridLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclesView.setHasFixedSize(true);

        /*
         * The MovieAdapter is responsible for linking our movie data with the Views that
         * will end up displaying our movie data.
         */
        mMovieAdapter = new MovieAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclesView.setAdapter(mMovieAdapter);


        mDB = AppDatabase.getInstance(getApplicationContext());

        /*
         * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
         * on our Loader at a later point in time through the support LoaderManager.
         */
        int loaderId = MOVIE_LOADER_ID;

        /*
         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * PopularMovieList. (implements LoaderCallbacks<PopularMovieList>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderManager.LoaderCallbacks<PopularMovieList> callback = MainActivity.this;

        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we add the URL via the bundle
         */
        Bundle movieBundle = new Bundle();
        movieBundle.putString(MOVIE_URL_EXTRA, MOVIE_DB_URL_POPULAR);

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(loaderId, movieBundle, callback);



    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<PopularMovieList> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<PopularMovieList>(this) {

            /* This String array will hold and help cache our movie data */
            PopularMovieList mMovieData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {

                /* If no arguments were passed, we don't have a query to perform. Simply return. */

                if (loaderArgs == null) {
                    return;
                }

                /*
                 * If we already have cached results, just deliver them now. If we don't have any
                 * cached results, force a load.
                 */
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    /*
                     * When we initially begin loading in the background, we want to display the
                     * loading indicator to the user
                     */
                    mLoadingIndicator.setVisibility(View.VISIBLE);
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
            public PopularMovieList loadInBackground() {

                String url = loaderArgs.getString(MOVIE_URL_EXTRA);


                URL movieRequestUrl = NetworkUtils.buidlUrl(url);

                try {
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieRequestUrl);

                    return MovieDbJsonUtils.parsePopMovieListJson(jsonMovieResponse);
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
            public void deliverResult(PopularMovieList data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }


    private void retrieveMoviesFromDatabase(){
        final LiveData<List<MovieEntry>> movies = mDB.movieDao().loadAllMovies();
        movies.observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                List<Movie> movies = MapperUtils.MovieDaoListToMovieList(movieEntries);
                PopularMovieList popularMovieList = new PopularMovieList(movies);
                mMovieAdapter.setMovieDataa(popularMovieList);
            }
        });
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<PopularMovieList> loader, PopularMovieList data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (null == data) {
            showErrorMessage();
        } else {
            mMovieAdapter.setMovieDataa(data);
            showMovieData();
        }
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        mMovieAdapter.setMovieDataa(null);
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<PopularMovieList> loader) {
        /*
         * We aren't using this method in our application, but we are required to Override
         * it to implement the LoaderCallbacks<PopularMovieList> interface
         */
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMovieData(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclesView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage(){
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mRecyclesView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        getMenuInflater().inflate(R.menu.main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        Bundle movieBundle = new Bundle();


        switch (itemThatWasClickedId){
            case R.id.popularity:
                invalidateData();
                movieBundle.putString(MOVIE_URL_EXTRA, MOVIE_DB_URL_POPULAR);
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, movieBundle, this);
                return true;
            case R.id.topRated:
                movieBundle.putString(MOVIE_URL_EXTRA, MOVIE_DB_URL_TOP_RATED);
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, movieBundle, this);
                return true;
            case R.id.favorite:
                retrieveMoviesFromDatabase();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is for responding to clicks from our grid.
     *
     * @param Movie movie describing movie details for a particular movie
     */
    @Override
    public void onClick(Movie movie){
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(EXTRA_MOVIE, movie );
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }


}
