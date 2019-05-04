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

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import static android.example.popularmoviesone.DetailActivity.EXTRA_MOVIE;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclesView;
    private MovieAdapter mMovieAdapter;


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

        new FetchMovieTask().execute();

    }

    @Override
    public void onClick(Movie movie){
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(EXTRA_MOVIE, movie );
        startActivity(intentToStartDetailActivity);
    }

    class FetchMovieTask extends AsyncTask<String, Void, PopularMovieList> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected PopularMovieList doInBackground(String... strings) {
            URL movieRequestUrl = NetworkUtils.buidlUrl();

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
