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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import static android.example.popularmoviesone.DetailActivity.EXTRA_MOVIE;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, PopupMenu.OnMenuItemClickListener  {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclesView;
    private MovieAdapter mMovieAdapter;

    private static final String MOVIE_DB_URL_POPULAR = "https://api.themoviedb.org/3/movie/popular";
    private static final String MOVIE_DB_URL_TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclesView = findViewById(R.id.recyclerview_movies);

        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(this, 3);

        mRecyclesView.setLayoutManager(gridLayoutManager);

        mRecyclesView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclesView.setAdapter(mMovieAdapter);

        new FetchMovieTask().execute(MOVIE_DB_URL_POPULAR);

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
        }

        @Override
        protected PopularMovieList doInBackground(String... urls) {
            URL movieRequestUrl = NetworkUtils.buidlUrl(urls[0]);

            try{
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);


                PopularMovieList popularMovieList = MovieDbJsonUtils.parsePopMovieListJson(jsonMovieResponse);

                String[] movieTitles = new String[Objects.requireNonNull(popularMovieList).getResults().size()];

                for(int i = 0; i< popularMovieList.getResults().size(); i++){
                    movieTitles[i] = popularMovieList.getResults().get(i).getTitle();

                }
                Log.d(TAG, jsonMovieResponse);

                return popularMovieList;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(PopularMovieList titleData) {
            if (titleData != null) {
                mMovieAdapter.setMovieDataa(titleData);

            }
        }
    }


}
