package android.example.popularmoviesone;

import android.content.Context;
import android.content.Intent;
import android.example.popularmoviesone.model.Movie;
import android.example.popularmoviesone.model.PopularMovieList;
import android.example.popularmoviesone.utilities.MovieDbJsonUtils;
import android.example.popularmoviesone.utilities.NetworkUtils;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.net.URL;

import static android.example.popularmoviesone.DetailActivity.EXTRA_MOVIE;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, PopupMenu.OnMenuItemClickListener  {

    private RecyclerView mRecyclesView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;

    private static final String MOVIE_DB_URL_POPULAR = "https://api.themoviedb.org/3/movie/popular";
    private static final String MOVIE_DB_URL_TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclesView = findViewById(R.id.recyclerview_movies);
        mErrorMessageTextView = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_inidcator);

        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(this, 3);

        mRecyclesView.setLayoutManager(gridLayoutManager);

        mRecyclesView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclesView.setAdapter(mMovieAdapter);

        new FetchMovieTask().execute(MOVIE_DB_URL_POPULAR);

    }

    private void showMovieData(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclesView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mRecyclesView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        switch (itemThatWasClickedId){
            case R.id.popularity:
                new FetchMovieTask().execute(MOVIE_DB_URL_POPULAR);
                return true;
            case R.id.topRated:
                new FetchMovieTask().execute(MOVIE_DB_URL_TOP_RATED);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    class FetchMovieTask extends AsyncTask<String, Void, PopularMovieList> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected PopularMovieList doInBackground(String... urls) {
            URL movieRequestUrl = NetworkUtils.buidlUrl(urls[0]);

            try{
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);



                return MovieDbJsonUtils.parsePopMovieListJson(jsonMovieResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(PopularMovieList titleData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (titleData != null) {
                showMovieData();
                mMovieAdapter.setMovieDataa(titleData);
            } else {
                showErrorMessage();
            }
        }
    }


}
